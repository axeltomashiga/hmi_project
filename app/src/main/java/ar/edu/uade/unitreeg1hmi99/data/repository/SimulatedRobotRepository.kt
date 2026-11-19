package ar.edu.uade.unitreeg1hmi99.data.repository

import android.content.Context
import ar.edu.uade.unitreeg1hmi99.data.local.AppDatabase
import ar.edu.uade.unitreeg1hmi99.data.local.SharedPrefsSimulationStateStore
import ar.edu.uade.unitreeg1hmi99.data.local.SimulationSnapshot
import ar.edu.uade.unitreeg1hmi99.data.local.SimulationStateStore
import ar.edu.uade.unitreeg1hmi99.data.local.dao.EventLogDao
import ar.edu.uade.unitreeg1hmi99.data.local.dao.MissionHistoryDao
import ar.edu.uade.unitreeg1hmi99.data.local.entity.EventLogEntity
import ar.edu.uade.unitreeg1hmi99.data.local.entity.MissionHistoryEntity
import ar.edu.uade.unitreeg1hmi99.data.mapper.toDomain
import ar.edu.uade.unitreeg1hmi99.domain.model.AlertItem
import ar.edu.uade.unitreeg1hmi99.domain.model.AlertSeverity
import ar.edu.uade.unitreeg1hmi99.domain.model.BuildingMap
import ar.edu.uade.unitreeg1hmi99.domain.model.CommandRecord
import ar.edu.uade.unitreeg1hmi99.domain.model.CommandStatus
import ar.edu.uade.unitreeg1hmi99.domain.model.CommandType
import ar.edu.uade.unitreeg1hmi99.domain.model.ConnectionState
import ar.edu.uade.unitreeg1hmi99.domain.model.EventLogRecord
import ar.edu.uade.unitreeg1hmi99.domain.model.FloorMap
import ar.edu.uade.unitreeg1hmi99.domain.model.MapWaypoint
import ar.edu.uade.unitreeg1hmi99.domain.model.MissionRecord
import ar.edu.uade.unitreeg1hmi99.domain.model.OperationalState
import ar.edu.uade.unitreeg1hmi99.domain.model.TelemetryData
import ar.edu.uade.unitreeg1hmi99.domain.repository.RobotRepository
import ar.edu.uade.unitreeg1hmi99.domain.usecase.ValidateRobotCommandUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.random.Random

class SimulatedRobotRepository(
    private val missionDao: MissionHistoryDao,
    private val eventDao: EventLogDao,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    override val floorMap: FloorMap = BuildingMap.default,
    private val stateStore: SimulationStateStore? = null,
    private val validateCommandUseCase: ValidateRobotCommandUseCase = ValidateRobotCommandUseCase()
) : RobotRepository {

    private val baseStation = floorMap.base

    private val _telemetry = MutableStateFlow(
        TelemetryData(
            robotId = "Unitree-G1-Alpha",
            operationalState = OperationalState.STANDBY,
            zoneName = baseStation.name,
            posX = baseStation.x,
            posY = baseStation.y,
            totalWaypoints = floorMap.routeFor("MIS-A").size,
            isCharging = true
        )
    )
    override val telemetry: StateFlow<TelemetryData> = _telemetry.asStateFlow()

    private val _lastCommand = MutableStateFlow<CommandRecord?>(null)
    override val lastCommand: StateFlow<CommandRecord?> = _lastCommand.asStateFlow()

    // Banderas de simulación observables
    private val _isForcedOffline = MutableStateFlow(false)
    override val isForcedOffline: StateFlow<Boolean> = _isForcedOffline.asStateFlow()

    private val _isForcedRejection = MutableStateFlow(false)
    override val isForcedRejection: StateFlow<Boolean> = _isForcedRejection.asStateFlow()

    private var missionStartTime: Long = 0L
    private var maxRecordedGas: Double = 14.2
    private var maxRecordedTemp: Double = 21.5

    // Estado cinemático (protegido por simLock: lo tocan el ticker y los comandos)
    private val simLock = Any()
    private val routeQueue = ArrayDeque<MapWaypoint>()   // waypoints pendientes de alcanzar
    private var currentRoute: List<MapWaypoint> = floorMap.waypoints
    private var pathTotalDistance = 0f                // longitud de la ruta activa (m)
    private var returnStartProgress = 0f              // progreso al iniciar el retorno a base
    private var reachedCount = 0                      // waypoints de la misión ya alcanzados
    private var dwellRemainingSec = 0f                // pausa de inspección en el waypoint actual
    private var batteryAccum = 0f                     // fracción de % de batería acumulada
    private var tickCounter = 0L

    private var simulationTickerJob: Job? = null

    init {
        val restored = restoreState()
        startSimulationLoop()
        if (!restored) {
            logEventAsync("SYSTEM", "Simulador Unitree G1 Inicializado", "Listo para supervisión offline", "INFO")
        }
    }

    /** Avanza la simulación N ticks sin esperar al reloj. Solo para tests. */
    internal fun advanceTicks(n: Int) {
        repeat(n) { tickSimulation() }
    }

    private fun startSimulationLoop() {
        simulationTickerJob?.cancel()
        simulationTickerJob = scope.launch {
            while (isActive) {
                delay(TICK_MS)
                tickSimulation()
            }
        }
    }

    private fun tickSimulation() {
        val snapshotToSave: SimulationSnapshot? = synchronized(simLock) {
            if (_isForcedOffline.value) {
                // Cuando está desconectado, congelamos los datos y marcamos como antiguo (stale)
                _telemetry.value = _telemetry.value.copy(
                    connectionState = ConnectionState.OFFLINE,
                    isDataStale = true
                )
                return@synchronized null
            }

            val current = _telemetry.value
            val now = System.currentTimeMillis()
            tickCounter++
            val isSlowTick = tickCounter % SLOW_TICK_EVERY == 0L
            val dtSim = TICK_SECONDS * TIME_SCALE

            // 1. Cinemática: avance continuo sobre la ruta
            var opState = current.operationalState
            var posX = current.posX
            var posY = current.posY
            var heading = current.headingDeg
            var speed = current.speedMps
            var isMoving = current.isMoving
            var progress = current.missionProgress
            var zone = current.zoneName
            var waypointIdx = current.currentWaypoint
            var justCompleted: String? = null
            var transitionOccurred = false

            when (opState) {
                OperationalState.PATROLLING -> {
                    if (dwellRemainingSec > 0f) {
                        // Guardia: el robot se detiene en el waypoint y barre el entorno girando sobre su eje
                        dwellRemainingSec -= dtSim
                        heading = normalizeDeg(heading + SCAN_DEG_PER_SEC * dtSim)
                        isMoving = false
                        speed = 0f
                    } else {
                        speed = PATROL_SPEED_MPS + (Random.nextFloat() * 0.1f - 0.05f)
                        isMoving = true
                        val budget = PATROL_SPEED_MPS * dtSim
                        if (routeQueue.isNotEmpty()) {
                            val target = routeQueue.first()
                            val dx = target.x - posX
                            val dy = target.y - posY
                            val dist = hypot(dx, dy)
                            if (dist > 0.001f) heading = normalizeDeg(Math.toDegrees(atan2(dy, dx).toDouble()).toFloat())
                            zone = target.name
                            if (dist <= budget) {
                                posX = target.x
                                posY = target.y
                                routeQueue.removeFirst()
                                reachedCount++
                                waypointIdx = reachedCount
                                transitionOccurred = true
                                if (routeQueue.isNotEmpty()) {
                                    // Llegó: se detiene de inmediato e inicia la pausa de guardia
                                    dwellRemainingSec = DWELL_SECONDS
                                    isMoving = false
                                    speed = 0f
                                }
                            } else {
                                posX += dx / dist * budget
                                posY += dy / dist * budget
                                waypointIdx = reachedCount + 1
                            }
                        }

                        if (routeQueue.isEmpty()) {
                            // Misión completada: el robot frena en el último punto (fuera de la base)
                            progress = 100f
                            opState = OperationalState.STANDBY
                            isMoving = false
                            speed = 0f
                            waypointIdx = currentRoute.size
                            justCompleted = current.missionName ?: "Ronda de Inspección"
                            transitionOccurred = true
                            persistMissionResult("COMPLETED", currentRoute.size, "Ronda finalizada con éxito. Esperando decisión: retorno a base o nueva misión.")
                        } else {
                            progress = (100f * (1f - remainingDistance(posX, posY) / pathTotalDistance.coerceAtLeast(0.01f)))
                                .coerceIn(0f, 99.9f)
                        }
                    }
                }

                OperationalState.RETURNING_HOME -> {
                    speed = RETURN_SPEED_MPS
                    isMoving = true
                    val budget = RETURN_SPEED_MPS * dtSim
                    if (routeQueue.isNotEmpty()) {
                        val target = routeQueue.first()
                        val dx = target.x - posX
                        val dy = target.y - posY
                        val dist = hypot(dx, dy)
                        if (dist > 0.001f) heading = normalizeDeg(Math.toDegrees(atan2(dy, dx).toDouble()).toFloat())
                        zone = target.name
                        if (dist <= budget) {
                            posX = target.x
                            posY = target.y
                            routeQueue.removeFirst()
                            transitionOccurred = true
                        } else {
                            posX += dx / dist * budget
                            posY += dy / dist * budget
                        }
                    }
                    if (routeQueue.isEmpty()) {
                        // Llegó a base
                        progress = 0f
                        opState = OperationalState.STANDBY
                        isMoving = false
                        speed = 0f
                        zone = baseStation.name
                        reachedCount = 0
                        waypointIdx = 0
                        transitionOccurred = true
                        logEventAsync("SAFETY", "Retorno a Base Completado", "Robot asegurado en Estación Base de Carga. Iniciando regeneración de batería.", "INFO")
                    } else {
                        progress = (returnStartProgress * remainingDistance(posX, posY) / pathTotalDistance.coerceAtLeast(0.01f))
                            .coerceIn(0f, 100f)
                    }
                }

                OperationalState.PAUSED, OperationalState.EMERGENCY_STOP, OperationalState.STANDBY, OperationalState.FAULT -> {
                    isMoving = false
                    speed = 0f
                }
            }

            // Personas en áreas restringidas: la guardia las detecta al detenerse a inspeccionar
            // Oficinas (2) o el Depósito (1). Solo se informa el conteo, nunca imágenes (privacidad).
            val persons = if (opState == OperationalState.PATROLLING && dwellRemainingSec > 0f) {
                when {
                    zone.contains("Oficinas") -> 2
                    zone.contains("Depósito") -> 1
                    else -> 0
                }
            } else 0

            // 2. Batería: desgaste en patrullaje y recarga en base (acumulador fraccional)
            val isAtBase = hypot(posX - baseStation.x, posY - baseStation.y) < AT_BASE_RADIUS_M
            val isCharging = isAtBase && opState == OperationalState.STANDBY
            var newBattery = current.batteryPercentage
            when {
                isCharging -> batteryAccum += CHARGE_PCT_PER_SEC * TICK_SECONDS
                opState == OperationalState.PATROLLING -> batteryAccum -= DRAIN_PCT_PER_SEC * TICK_SECONDS
                else -> batteryAccum = 0f
            }
            while (batteryAccum >= 1f) { newBattery = (newBattery + 1).coerceAtMost(100); batteryAccum -= 1f }
            while (batteryAccum <= -1f) { newBattery = (newBattery - 1).coerceAtLeast(1); batteryAccum += 1f }

            // Alerta automática de batería baja si cae a 15% o menos
            val alerts = current.activeAlerts.toMutableList()
            if (newBattery <= 15 && alerts.none { it.code == "BATTERY_LOW" }) {
                val alert = AlertItem(
                    id = UUID.randomUUID().toString(),
                    code = "BATTERY_LOW",
                    title = "Batería Crítica",
                    message = "Nivel de batería en $newBattery%. Se sugiere regresar a punto seguro.",
                    severity = AlertSeverity.WARNING
                )
                alerts.add(alert)
                transitionOccurred = true
                logEventAsync("ALERT", alert.title, alert.message, "WARNING")
            }

            // 3. Variables de servicio y enlace: se refrescan 1 vez por segundo
            val hasGasAlert = alerts.any { it.code == "GAS_LEAK" && !it.acknowledged }
            var currentGas = current.smokeGasPpm
            var currentTemp = current.ambientTemperatureCelsius
            var rssi = current.rssiDbm
            var latency = current.latencyMs
            if (isSlowTick) {
                if (!hasGasAlert) {
                    currentGas = 12.0 + Random.nextDouble() * 3.0
                    currentTemp = 21.0 + Random.nextDouble() * 1.5
                }
                rssi = -55 + Random.nextInt(-4, 5)
                latency = 24L + Random.nextLong(0, 10)
            }

            if (currentGas > maxRecordedGas) maxRecordedGas = currentGas
            if (currentTemp > maxRecordedTemp) maxRecordedTemp = currentTemp

            _telemetry.value = current.copy(
                timestamp = now,
                connectionState = ConnectionState.ONLINE,
                rssiDbm = rssi,
                latencyMs = latency,
                batteryPercentage = newBattery,
                batteryVoltage = 24.0f + (newBattery / 100f) * 1.2f,
                isCharging = isCharging,
                operationalState = opState,
                missionProgress = progress,
                currentWaypoint = waypointIdx,
                waypointsReached = reachedCount,
                detectedPersonsCount = persons,
                zoneName = zone,
                posX = posX,
                posY = posY,
                headingDeg = heading,
                speedMps = speed,
                isMoving = isMoving,
                activeAlerts = alerts,
                lastUpdateTimestamp = now,
                isDataStale = false,
                smokeGasPpm = Math.round(currentGas * 10.0) / 10.0,
                ambientTemperatureCelsius = Math.round(currentTemp * 10.0) / 10.0,
                justCompletedMissionName = justCompleted ?: current.justCompletedMissionName
            )

            if (transitionOccurred || isSlowTick) snapshotLocked() else null
        }

        snapshotToSave?.let { stateStore?.save(it, sync = false) }
    }

    // --- Utilidades de cinemática ---

    private fun normalizeDeg(deg: Float): Float = ((deg % 360f) + 360f) % 360f

    /** Distancia restante desde (x, y) pasando por todos los puntos pendientes de la ruta. */
    private fun remainingDistance(x: Float, y: Float): Float {
        var total = 0f
        var px = x
        var py = y
        for (w in routeQueue) {
            total += hypot(w.x - px, w.y - py)
            px = w.x
            py = w.y
        }
        return total
    }

    /** Define una nueva ruta pendiente y fija su longitud total (base del cálculo de progreso). */
    private fun setRoute(points: List<MapWaypoint>, fromX: Float, fromY: Float) {
        routeQueue.clear()
        routeQueue.addAll(points)
        pathTotalDistance = remainingDistance(fromX, fromY)
        dwellRemainingSec = 0f
    }

    override suspend fun sendCommand(type: CommandType, parameters: Map<String, String>): CommandRecord {
        val cmdId = "CMD-${UUID.randomUUID().toString().take(8).uppercase()}"
        val initialRecord = CommandRecord(
            id = cmdId,
            type = type,
            status = CommandStatus.PENDING,
            message = "Enviando comando al robot..."
        )
        _lastCommand.value = initialRecord

        // 1. Simulación de latencia de transmisión
        delay(450L)

        // 2. Simulación de Desconexión (Escenario 3: Timeout sin respuesta)
        if (_isForcedOffline.value) {
            delay(1500L)
            val noResponse = CommandRecord(
                id = cmdId,
                type = type,
                status = CommandStatus.NO_RESPONSE,
                message = "Sin respuesta del robot. Verifique el enlace de comunicaciones."
            )
            _lastCommand.value = noResponse
            saveStateNow(sync = false)
            logEventAsync("COMMAND", "Comando sin respuesta", "Orden ${type.name} falló por timeout (OFFLINE)", "WARNING")
            return noResponse
        }

        // 3. Simulación de Rechazo Forzado para pruebas
        if (_isForcedRejection.value) {
            val rejected = CommandRecord(
                id = cmdId,
                type = type,
                status = CommandStatus.REJECTED,
                message = "Orden rechazada por el robot: modo de simulación de rechazos activo."
            )
            _lastCommand.value = rejected
            saveStateNow(sync = false)
            logEventAsync("COMMAND", "Comando Rechazado", "Orden ${type.name} rechazada por política", "WARNING")
            return rejected
        }

        // 4. Validación de condiciones de negocio delegada al Caso de Uso de Dominio
        val current = _telemetry.value
        val (isAccepted, rejectionReason) = validateCommandUseCase.validate(
            type = type,
            state = current,
            baseStation = baseStation,
            atBaseRadiusM = AT_BASE_RADIUS_M
        )

        if (!isAccepted) {
            val rejected = CommandRecord(
                id = cmdId,
                type = type,
                status = CommandStatus.REJECTED,
                message = rejectionReason
            )
            _lastCommand.value = rejected
            saveStateNow(sync = false)
            logEventAsync("COMMAND", "Orden Rechazada", "Comando ${type.name}: $rejectionReason", "WARNING")
            return rejected
        }

        // 5. Ejecución del comando aceptado (CONFIRMED)
        val confirmed = CommandRecord(
            id = cmdId,
            type = type,
            status = CommandStatus.CONFIRMED,
            message = "Orden ejecutada con éxito."
        )
        executeCommandEffect(type, parameters, confirmed)
        logEventAsync("COMMAND", "Orden Confirmada", "Comando ${type.name} ejecutado", "INFO")
        return confirmed
    }

    private fun executeCommandEffect(
        type: CommandType,
        parameters: Map<String, String>,
        confirmedRecord: CommandRecord
    ) {
        val snapshot = synchronized(simLock) {
            val current = _telemetry.value
            when (type) {
                CommandType.START_MISSION -> {
                    missionStartTime = System.currentTimeMillis()
                    maxRecordedGas = 14.2
                    maxRecordedTemp = 21.5
                    val mName = parameters["missionName"] ?: "Ronda de Inspección Integral"
                    val mId = parameters["missionId"] ?: "MIS-${UUID.randomUUID().toString().take(6).uppercase()}"
                    currentRoute = floorMap.routeFor(mId)
                    reachedCount = 0
                    batteryAccum = 0f
                    setRoute(currentRoute, current.posX, current.posY)
                    _telemetry.value = current.copy(
                        operationalState = OperationalState.PATROLLING,
                        missionId = mId,
                        missionName = mName,
                        missionProgress = 0f,
                        currentWaypoint = 1,
                        totalWaypoints = currentRoute.size,
                        waypointsReached = 0,
                        isCharging = false,
                        isMoving = true,
                        speedMps = 0.85f,
                        justCompletedMissionName = null
                    )
                }

                CommandType.PAUSE_MISSION -> {
                    _telemetry.value = current.copy(
                        operationalState = OperationalState.PAUSED,
                        isMoving = false,
                        speedMps = 0f
                    )
                }

                CommandType.RESUME_MISSION -> {
                    _telemetry.value = current.copy(
                        operationalState = OperationalState.PATROLLING,
                        isMoving = true,
                        speedMps = 0.85f
                    )
                }

                CommandType.CANCEL_MISSION -> {
                    val completed = current.waypointsReached
                    persistMissionResult("CANCELLED", completed, "Misión cancelada por el operador.")
                    routeQueue.clear()
                    dwellRemainingSec = 0f
                    _telemetry.value = current.copy(
                        operationalState = OperationalState.STANDBY,
                        isMoving = false,
                        speedMps = 0f
                    )
                }

                CommandType.RETURN_TO_SAFE_POINT -> {
                    // Vuelve sobre sus pasos: waypoints ya visitados en orden inverso y luego la base
                    val backtrack = currentRoute.take(reachedCount).reversed() + baseStation
                    returnStartProgress = current.missionProgress
                    setRoute(backtrack, current.posX, current.posY)
                    _telemetry.value = current.copy(
                        operationalState = OperationalState.RETURNING_HOME,
                        isCharging = false,
                        isMoving = true,
                        speedMps = 0.70f,
                        justCompletedMissionName = null
                    )
                }

                CommandType.OPERATIONAL_STOP -> {
                    _telemetry.value = current.copy(
                        operationalState = OperationalState.EMERGENCY_STOP,
                        isMoving = false,
                        speedMps = 0f
                    )
                }

                CommandType.ACKNOWLEDGE_ALERT -> {
                    val updatedAlerts = current.activeAlerts.map { it.copy(acknowledged = true) }
                    _telemetry.value = current.copy(activeAlerts = updatedAlerts)
                }
            }
            _lastCommand.value = confirmedRecord
            snapshotLocked()
        }
        stateStore?.save(snapshot, sync = false)
    }

    override suspend fun acknowledgeAlert(alertId: String) {
        val snapshot = synchronized(simLock) {
            val current = _telemetry.value
            val updated = current.activeAlerts.map {
                if (it.id == alertId) it.copy(acknowledged = true) else it
            }
            _telemetry.value = current.copy(activeAlerts = updated)
            snapshotLocked()
        }
        stateStore?.save(snapshot, sync = false)
        logEventAsync("ALERT", "Alerta Reconocida", "Operador reconoció la alerta ID $alertId", "INFO")
    }

    override fun dismissMissionCompleted() {
        val snapshot = synchronized(simLock) {
            _telemetry.value = _telemetry.value.copy(justCompletedMissionName = null)
            snapshotLocked()
        }
        stateStore?.save(snapshot, sync = false)
    }

    override fun getMissionHistory(): Flow<List<MissionRecord>> =
        missionDao.getAllMissions().map { entities -> entities.map { it.toDomain() } }

    override fun getEventLogs(): Flow<List<EventLogRecord>> =
        eventDao.getAllEvents().map { entities -> entities.map { it.toDomain() } }

    override suspend fun clearHistory() {
        kotlinx.coroutines.withContext(Dispatchers.IO) {
            missionDao.clearHistory()
            eventDao.clearEvents()
        }
    }

    // --- Disparadores de los 5 Escenarios de Prueba ---

    override fun toggleDisconnection(forceOffline: Boolean) {
        val snapshot = synchronized(simLock) {
            _isForcedOffline.value = forceOffline
            if (!forceOffline) {
                _telemetry.value = _telemetry.value.copy(
                    connectionState = ConnectionState.ONLINE,
                    isDataStale = false,
                    lastUpdateTimestamp = System.currentTimeMillis()
                )
            } else {
                _telemetry.value = _telemetry.value.copy(
                    connectionState = ConnectionState.OFFLINE,
                    isDataStale = true
                )
            }
            snapshotLocked()
        }
        stateStore?.save(snapshot, sync = false)
        if (forceOffline) {
            logEventAsync("DISCONNECTION", "Pérdida de Enlace", "Conexión con Unitree G1 interrumpida (Simulado)", "WARNING")
        } else {
            logEventAsync("DISCONNECTION", "Enlace Restablecido", "Comunicaciones con Unitree G1 recuperadas", "INFO")
        }
    }

    override fun triggerLowBattery(percentage: Int) {
        val alert = AlertItem(
            id = UUID.randomUUID().toString(),
            code = "BATTERY_LOW",
            title = "Batería Baja Crítica",
            message = "Nivel de batería cayó a $percentage%. Retorno urgente requerido.",
            severity = AlertSeverity.WARNING
        )
        val snapshot = synchronized(simLock) {
            val current = _telemetry.value
            val alerts = current.activeAlerts.filterNot { it.code == "BATTERY_LOW" } + alert
            _telemetry.value = current.copy(
                batteryPercentage = percentage,
                activeAlerts = alerts
            )
            snapshotLocked()
        }
        stateStore?.save(snapshot, sync = false)
        logEventAsync("ALERT", alert.title, alert.message, "WARNING")
    }

    override fun triggerObstacleIncident() {
        val alert = AlertItem(
            id = UUID.randomUUID().toString(),
            code = "OBSTACLE_BLOCKED",
            title = "Obstáculo Detectado en Ruta",
            message = "Trayectoria bloqueada por más de 10s. Requiere decisión del operador.",
            severity = AlertSeverity.WARNING
        )
        val snapshot = synchronized(simLock) {
            val current = _telemetry.value
            _telemetry.value = current.copy(
                operationalState = OperationalState.PAUSED,
                isMoving = false,
                speedMps = 0f,
                activeAlerts = current.activeAlerts + alert
            )
            snapshotLocked()
        }
        stateStore?.save(snapshot, sync = false)
        logEventAsync("SAFETY", alert.title, alert.message, "WARNING")
    }

    override fun triggerServiceAlert(gasPpm: Double, tempCelsius: Double) {
        val alert = AlertItem(
            id = UUID.randomUUID().toString(),
            code = "GAS_LEAK",
            title = "Peligro: Fuga de Gas / Sobrecalentamiento",
            message = "Lectura anómala en tablero eléctrico (${gasPpm} ppm, ${tempCelsius}°C). Freno preventivo.",
            severity = AlertSeverity.CRITICAL
        )
        val snapshot = synchronized(simLock) {
            val current = _telemetry.value
            _telemetry.value = current.copy(
                operationalState = OperationalState.PAUSED,
                isMoving = false,
                speedMps = 0f,
                smokeGasPpm = gasPpm,
                ambientTemperatureCelsius = tempCelsius,
                activeAlerts = current.activeAlerts + alert
            )
            snapshotLocked()
        }
        stateStore?.save(snapshot, sync = false)
        logEventAsync("SAFETY", alert.title, alert.message, "CRITICAL")
    }

    override fun toggleForceRejectCommands(reject: Boolean) {
        val snapshot = synchronized(simLock) {
            _isForcedRejection.value = reject
            snapshotLocked()
        }
        stateStore?.save(snapshot, sync = false)
    }

    override fun resetSimulation() {
        synchronized(simLock) {
            _isForcedOffline.value = false
            _isForcedRejection.value = false
            missionStartTime = 0L
            maxRecordedGas = 14.2
            maxRecordedTemp = 21.5
            routeQueue.clear()
            currentRoute = floorMap.waypoints
            pathTotalDistance = 0f
            returnStartProgress = 0f
            reachedCount = 0
            dwellRemainingSec = 0f
            batteryAccum = 0f
            tickCounter = 0L
            _telemetry.value = TelemetryData(
                robotId = "Unitree-G1-Alpha",
                operationalState = OperationalState.STANDBY,
                zoneName = baseStation.name,
                posX = baseStation.x,
                posY = baseStation.y,
                totalWaypoints = floorMap.routeFor("MIS-A").size,
                isCharging = true
            )
            _lastCommand.value = null
        }
        stateStore?.clear()
        logEventAsync("SYSTEM", "Simulación Reiniciada", "Valores reestablecidos a valores de fábrica", "INFO")
    }

    override fun saveStateNow(sync: Boolean) {
        val snapshot = synchronized(simLock) { snapshotLocked() }
        stateStore?.save(snapshot, sync)
    }

    private fun snapshotLocked(): SimulationSnapshot = SimulationSnapshot(
        telemetry = _telemetry.value,
        lastCommand = _lastCommand.value,
        isForcedOffline = _isForcedOffline.value,
        isForcedRejection = _isForcedRejection.value,
        missionStartTime = missionStartTime,
        maxRecordedGas = maxRecordedGas,
        maxRecordedTemp = maxRecordedTemp,
        routeQueue = routeQueue.toList(),
        currentRoute = currentRoute.toList(),
        pathTotalDistance = pathTotalDistance,
        returnStartProgress = returnStartProgress,
        reachedCount = reachedCount,
        dwellRemainingSec = dwellRemainingSec,
        batteryAccum = batteryAccum,
        tickCounter = tickCounter
    )

    private fun restoreState(): Boolean {
        val snapshot = stateStore?.load() ?: return false
        synchronized(simLock) {
            _isForcedOffline.value = snapshot.isForcedOffline
            _isForcedRejection.value = snapshot.isForcedRejection
            missionStartTime = snapshot.missionStartTime
            maxRecordedGas = snapshot.maxRecordedGas
            maxRecordedTemp = snapshot.maxRecordedTemp
            routeQueue.clear()
            routeQueue.addAll(snapshot.routeQueue)
            currentRoute = snapshot.currentRoute.ifEmpty { floorMap.routeFor(snapshot.telemetry.missionId) }
            pathTotalDistance = snapshot.pathTotalDistance
            returnStartProgress = snapshot.returnStartProgress
            reachedCount = snapshot.reachedCount
            dwellRemainingSec = snapshot.dwellRemainingSec
            batteryAccum = snapshot.batteryAccum
            tickCounter = snapshot.tickCounter
            _telemetry.value = snapshot.telemetry
            _lastCommand.value = snapshot.lastCommand?.let { cmd ->
                if (cmd.status == CommandStatus.PENDING) {
                    cmd.copy(
                        status = CommandStatus.NO_RESPONSE,
                        message = "La aplicación se cerró antes de recibir confirmación."
                    )
                } else {
                    cmd
                }
            }
        }
        return true
    }

    private fun persistMissionResult(status: String, waypointsCompleted: Int, notes: String) {
        val current = _telemetry.value
        val startTime = if (missionStartTime > 0L) missionStartTime else System.currentTimeMillis() - 60000L
        scope.launch(Dispatchers.IO) {
            missionDao.insertMission(
                MissionHistoryEntity(
                    missionId = current.missionId ?: "MIS-001",
                    missionName = current.missionName ?: "Ronda de Inspección",
                    startTime = startTime,
                    endTime = System.currentTimeMillis(),
                    status = status,
                    waypointsCompleted = waypointsCompleted,
                    totalWaypoints = currentRoute.size,
                    maxGasPpm = maxRecordedGas,
                    maxTempCelsius = maxRecordedTemp,
                    incidentsCount = current.activeAlerts.size,
                    notes = notes
                )
            )
            logEventAsync("MISSION", "Misión $status", "Misión ${current.missionId} finalizada con estado $status", "INFO")
        }
    }

    private fun logEventAsync(type: String, title: String, details: String, severity: String) {
        scope.launch(Dispatchers.IO) {
            eventDao.insertEvent(
                EventLogEntity(
                    eventType = type,
                    title = title,
                    details = details,
                    severity = severity
                )
            )
        }
    }

    companion object {
        private const val TICK_MS = 200L
        private const val TICK_SECONDS = TICK_MS / 1000f
        private const val SLOW_TICK_EVERY = 5L            // 1 s: variables ambientales y de enlace

        /** Compresión del tiempo de simulación para que una ronda dure ~40 s en la demo. */
        private const val TIME_SCALE = 2f
        private const val PATROL_SPEED_MPS = 0.85f
        private const val RETURN_SPEED_MPS = 0.70f
        private const val DWELL_SECONDS = 3f              // pausa de guardia en cada waypoint
        private const val SCAN_DEG_PER_SEC = 120f         // giro del barrido: 360° durante la pausa
        private const val AT_BASE_RADIUS_M = 0.5f

        private const val DRAIN_PCT_PER_SEC = 0.3f        // desgaste en patrullaje
        private const val CHARGE_PCT_PER_SEC = 2f         // recarga en base

        @Volatile
        private var INSTANCE: SimulatedRobotRepository? = null

        fun getInstance(context: Context): SimulatedRobotRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: run {
                    val appContext = context.applicationContext
                    val database = AppDatabase.getInstance(appContext)
                    SimulatedRobotRepository(
                        missionDao = database.missionHistoryDao(),
                        eventDao = database.eventLogDao(),
                        stateStore = SharedPrefsSimulationStateStore(appContext)
                    ).also { INSTANCE = it }
                }
            }
        }
    }
}
