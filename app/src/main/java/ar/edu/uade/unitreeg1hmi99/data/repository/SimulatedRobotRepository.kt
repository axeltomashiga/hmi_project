package ar.edu.uade.unitreeg1hmi99.data.repository

import ar.edu.uade.unitreeg1hmi99.data.local.dao.EventLogDao
import ar.edu.uade.unitreeg1hmi99.data.local.dao.MissionHistoryDao
import ar.edu.uade.unitreeg1hmi99.data.local.entity.EventLogEntity
import ar.edu.uade.unitreeg1hmi99.data.local.entity.MissionHistoryEntity
import ar.edu.uade.unitreeg1hmi99.data.model.AlertItem
import ar.edu.uade.unitreeg1hmi99.data.model.AlertSeverity
import ar.edu.uade.unitreeg1hmi99.data.model.CommandRecord
import ar.edu.uade.unitreeg1hmi99.data.model.CommandStatus
import ar.edu.uade.unitreeg1hmi99.data.model.CommandType
import ar.edu.uade.unitreeg1hmi99.data.model.ConnectionState
import ar.edu.uade.unitreeg1hmi99.data.model.OperationalState
import ar.edu.uade.unitreeg1hmi99.data.model.TelemetryData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

class SimulatedRobotRepository(
    private val missionDao: MissionHistoryDao,
    private val eventDao: EventLogDao,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : RobotRepository {

    private val baseStation = Waypoint("Estación Base de Carga", 0.0f, 0.0f)

    private val waypoints = listOf(
        Waypoint("Pabellón A - Hall Central", 2.0f, 1.0f),
        Waypoint("Pasillo de Aulas 101-105", 8.0f, 2.5f),
        Waypoint("Laboratorio de Robótica e IoT", 14.0f, 5.0f),
        Waypoint("Sala de Servidores y Tablero B", 18.0f, 8.0f),
        Waypoint("Salida de Emergencia Este", 22.0f, 12.0f),
        Waypoint("Depósito de Mantenimiento", 16.0f, 15.0f),
        Waypoint("Pasillo Sur - Oficinas", 9.0f, 10.0f),
        Waypoint("Punto Final de Inspección", 8.0f, 8.0f)
    )

    private val _telemetry = MutableStateFlow(
        TelemetryData(
            robotId = "Unitree-G1-Alpha",
            operationalState = OperationalState.STANDBY,
            zoneName = baseStation.name,
            posX = baseStation.x,
            posY = baseStation.y,
            totalWaypoints = waypoints.size,
            isCharging = true
        )
    )
    override val telemetry: StateFlow<TelemetryData> = _telemetry.asStateFlow()

    private val _lastCommand = MutableStateFlow<CommandRecord?>(null)
    override val lastCommand: StateFlow<CommandRecord?> = _lastCommand.asStateFlow()

    // Banderas de simulación
    private var isForcedOffline: Boolean = false
    private var isForcedRejection: Boolean = false
    private var missionStartTime: Long = 0L
    private var maxRecordedGas: Double = 14.2
    private var maxRecordedTemp: Double = 21.5

    private var simulationTickerJob: Job? = null

    init {
        startSimulationLoop()
        logEventAsync("SYSTEM", "Simulador Unitree G1 Inicializado", "Listo para supervisión offline", "INFO")
    }

    private fun startSimulationLoop() {
        simulationTickerJob?.cancel()
        simulationTickerJob = scope.launch {
            while (isActive) {
                delay(1000L)
                tickSimulation()
            }
        }
    }

    private suspend fun tickSimulation() {
        if (isForcedOffline) {
            // Cuando está desconectado, congelamos los datos y marcamos como antiguo (stale)
            _telemetry.value = _telemetry.value.copy(
                connectionState = ConnectionState.OFFLINE,
                isDataStale = true
            )
            return
        }

        val current = _telemetry.value
        val now = System.currentTimeMillis()

        // 1. Desgaste y recarga de batería en base
        val isAtBase = current.zoneName.contains("Base")
        val isCharging = isAtBase && (current.operationalState == OperationalState.STANDBY)
        val newBattery = when {
            isCharging -> {
                if (current.batteryPercentage < 100) (current.batteryPercentage + 2).coerceAtMost(100) else 100
            }
            current.operationalState == OperationalState.PATROLLING && Random.nextInt(100) < 30 ->
                (current.batteryPercentage - 1).coerceAtLeast(1)
            else -> current.batteryPercentage
        }

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
            logEventAsync("ALERT", alert.title, alert.message, "WARNING")
        }

        // 2. Progresión según estado operativo
        var opState = current.operationalState
        var progress = current.missionProgress
        var waypointIdx = current.currentWaypoint
        var zone = current.zoneName
        var posX = current.posX
        var posY = current.posY
        var speed = current.speedMps
        var isMoving = current.isMoving
        var heading = current.headingDeg
        var justCompleted: String? = null

        when (opState) {
            OperationalState.PATROLLING -> {
                isMoving = true
                speed = 0.85f + (Random.nextFloat() * 0.1f - 0.05f)
                progress += 3.5f

                if (progress >= 100f) {
                    // Misión completada: el robot frena en el último punto del recorrido (fuera de la base)
                    progress = 100f
                    opState = OperationalState.STANDBY
                    isMoving = false
                    speed = 0f
                    waypointIdx = waypoints.size
                    zone = waypoints.last().name
                    posX = waypoints.last().x
                    posY = waypoints.last().y

                    // Notificar fin de misión para decisión del operador
                    justCompleted = current.missionName ?: "Ronda de Inspección"

                    // Persistir misión en Room
                    persistMissionResult("COMPLETED", waypoints.size, "Ronda finalizada con éxito. Esperando decisión: retorno a base o nueva misión.")
                } else {
                    // Actualizar waypoint proporcional al progreso
                    val targetIdx = ((progress / 100f) * (waypoints.size - 1)).toInt().coerceIn(0, waypoints.size - 1)
                    waypointIdx = targetIdx + 1
                    val targetWaypoint = waypoints[targetIdx]
                    zone = targetWaypoint.name
                    posX = targetWaypoint.x
                    posY = targetWaypoint.y
                    heading = (heading + 15f) % 360f
                }
            }

            OperationalState.RETURNING_HOME -> {
                isMoving = true
                speed = 0.70f
                if (progress > 5f) {
                    progress -= 8f
                } else {
                    // Llegó a base
                    progress = 0f
                    opState = OperationalState.STANDBY
                    isMoving = false
                    speed = 0f
                    zone = baseStation.name
                    posX = baseStation.x
                    posY = baseStation.y
                    logEventAsync("SAFETY", "Retorno a Base Completado", "Robot asegurado en Estación Base de Carga. Iniciando regeneración de batería.", "INFO")
                }
            }

            OperationalState.PAUSED, OperationalState.EMERGENCY_STOP, OperationalState.STANDBY, OperationalState.FAULT -> {
                isMoving = false
                speed = 0f
            }
        }

        // 3. Variables de servicio (fluctuaciones ambientales normales si no hay alerta forzada)
        val hasGasAlert = alerts.any { it.code == "GAS_LEAK" && !it.acknowledged }
        val currentGas = if (hasGasAlert) current.smokeGasPpm else (12.0 + Random.nextDouble() * 3.0)
        val currentTemp = if (hasGasAlert) current.ambientTemperatureCelsius else (21.0 + Random.nextDouble() * 1.5)

        if (currentGas > maxRecordedGas) maxRecordedGas = currentGas
        if (currentTemp > maxRecordedTemp) maxRecordedTemp = currentTemp

        _telemetry.value = current.copy(
            timestamp = now,
            connectionState = ConnectionState.ONLINE,
            rssiDbm = -55 + Random.nextInt(-4, 5),
            latencyMs = 24L + Random.nextLong(0, 10),
            batteryPercentage = newBattery,
            batteryVoltage = 24.0f + (newBattery / 100f) * 1.2f,
            isCharging = isCharging,
            operationalState = opState,
            missionProgress = progress,
            currentWaypoint = waypointIdx,
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
        if (isForcedOffline) {
            delay(1500L)
            val noResponse = CommandRecord(
                id = cmdId,
                type = type,
                status = CommandStatus.NO_RESPONSE,
                message = "Sin respuesta del robot. Verifique el enlace de comunicaciones."
            )
            _lastCommand.value = noResponse
            logEventAsync("COMMAND", "Comando sin respuesta", "Orden ${type.name} falló por timeout (OFFLINE)", "WARNING")
            return noResponse
        }

        // 3. Simulación de Rechazo Forzado para pruebas
        if (isForcedRejection) {
            val rejected = CommandRecord(
                id = cmdId,
                type = type,
                status = CommandStatus.REJECTED,
                message = "Orden rechazada por el robot: modo de simulación de rechazos activo."
            )
            _lastCommand.value = rejected
            logEventAsync("COMMAND", "Comando Rechazado", "Orden ${type.name} rechazada por política", "WARNING")
            return rejected
        }

        // 4. Validación de condiciones de negocio
        val current = _telemetry.value
        val (isAccepted, rejectionReason) = validateCommandConditions(type, current)

        if (!isAccepted) {
            val rejected = CommandRecord(
                id = cmdId,
                type = type,
                status = CommandStatus.REJECTED,
                message = rejectionReason
            )
            _lastCommand.value = rejected
            logEventAsync("COMMAND", "Orden Rechazada", "Comando ${type.name}: $rejectionReason", "WARNING")
            return rejected
        }

        // 5. Ejecución del comando aceptado (CONFIRMED)
        executeCommandEffect(type, parameters)

        val confirmed = CommandRecord(
            id = cmdId,
            type = type,
            status = CommandStatus.CONFIRMED,
            message = "Orden ejecutada con éxito."
        )
        _lastCommand.value = confirmed
        logEventAsync("COMMAND", "Orden Confirmada", "Comando ${type.name} ejecutado", "INFO")
        return confirmed
    }

    private fun validateCommandConditions(type: CommandType, state: TelemetryData): Pair<Boolean, String> {
        return when (type) {
            CommandType.START_MISSION -> {
                if (state.operationalState != OperationalState.STANDBY) {
                    Pair(false, "El robot no está en espera (Estado actual: ${state.operationalState.name}).")
                } else if (state.batteryPercentage <= 15) {
                    Pair(false, "Batería insuficiente (${state.batteryPercentage}%). Mínimo requerido: 20%.")
                } else {
                    Pair(true, "")
                }
            }

            CommandType.PAUSE_MISSION -> {
                if (state.operationalState != OperationalState.PATROLLING) {
                    Pair(false, "No hay una misión en patrullaje para pausar.")
                } else {
                    Pair(true, "")
                }
            }

            CommandType.RESUME_MISSION -> {
                if (state.operationalState != OperationalState.PAUSED) {
                    Pair(false, "El robot no se encuentra en pausa.")
                } else {
                    Pair(true, "")
                }
            }

            CommandType.CANCEL_MISSION -> {
                if (state.operationalState != OperationalState.PATROLLING && state.operationalState != OperationalState.PAUSED) {
                    Pair(false, "No hay misión activa para cancelar.")
                } else {
                    Pair(true, "")
                }
            }

            CommandType.RETURN_TO_SAFE_POINT -> {
                if (state.operationalState == OperationalState.STANDBY && state.zoneName.contains("Base")) {
                    Pair(false, "El robot ya se encuentra en la base segura.")
                } else {
                    Pair(true, "")
                }
            }

            CommandType.OPERATIONAL_STOP -> {
                Pair(true, "")
            }

            CommandType.ACKNOWLEDGE_ALERT -> {
                if (state.activeAlerts.none { !it.acknowledged }) {
                    Pair(false, "No existen alertas pendientes de reconocimiento.")
                } else {
                    Pair(true, "")
                }
            }
        }
    }

    private fun executeCommandEffect(type: CommandType, parameters: Map<String, String>) {
        val current = _telemetry.value
        when (type) {
            CommandType.START_MISSION -> {
                missionStartTime = System.currentTimeMillis()
                maxRecordedGas = 14.2
                maxRecordedTemp = 21.5
                val mName = parameters["missionName"] ?: "Ronda de Inspección Integral"
                val mId = parameters["missionId"] ?: "MIS-${UUID.randomUUID().toString().take(6).uppercase()}"
                _telemetry.value = current.copy(
                    operationalState = OperationalState.PATROLLING,
                    missionId = mId,
                    missionName = mName,
                    missionProgress = 0f,
                    currentWaypoint = 1,
                    totalWaypoints = waypoints.size,
                    isMoving = true,
                    speedMps = 0.85f
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
                val completed = current.currentWaypoint
                persistMissionResult("CANCELLED", completed, "Misión cancelada por el operador.")
                _telemetry.value = current.copy(
                    operationalState = OperationalState.STANDBY,
                    isMoving = false,
                    speedMps = 0f
                )
            }

            CommandType.RETURN_TO_SAFE_POINT -> {
                _telemetry.value = current.copy(
                    operationalState = OperationalState.RETURNING_HOME,
                    isMoving = true,
                    speedMps = 0.70f
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
    }

    override suspend fun acknowledgeAlert(alertId: String) {
        val current = _telemetry.value
        val updated = current.activeAlerts.map {
            if (it.id == alertId) it.copy(acknowledged = true) else it
        }
        _telemetry.value = current.copy(activeAlerts = updated)
        logEventAsync("ALERT", "Alerta Reconocida", "Operador reconoció la alerta ID $alertId", "INFO")
    }

    override fun dismissMissionCompleted() {
        _telemetry.value = _telemetry.value.copy(justCompletedMissionName = null)
    }

    override fun getMissionHistory(): Flow<List<MissionHistoryEntity>> = missionDao.getAllMissions()

    override fun getEventLogs(): Flow<List<EventLogEntity>> = eventDao.getAllEvents()

    override suspend fun clearHistory() {
        kotlinx.coroutines.withContext(Dispatchers.IO) {
            missionDao.clearHistory()
            eventDao.clearEvents()
        }
    }

    // --- Disparadores de los 5 Escenarios de Prueba ---

    override fun toggleDisconnection(forceOffline: Boolean) {
        isForcedOffline = forceOffline
        if (forceOffline) {
            logEventAsync("DISCONNECTION", "Pérdida de Enlace", "Conexión con Unitree G1 interrumpida (Simulado)", "WARNING")
        } else {
            logEventAsync("DISCONNECTION", "Enlace Restablecido", "Comunicaciones con Unitree G1 recuperadas", "INFO")
            _telemetry.value = _telemetry.value.copy(
                connectionState = ConnectionState.ONLINE,
                isDataStale = false,
                lastUpdateTimestamp = System.currentTimeMillis()
            )
        }
    }

    override fun triggerLowBattery(percentage: Int) {
        val current = _telemetry.value
        val alert = AlertItem(
            id = UUID.randomUUID().toString(),
            code = "BATTERY_LOW",
            title = "Batería Baja Crítica",
            message = "Nivel de batería cayó a $percentage%. Retorno urgente requerido.",
            severity = AlertSeverity.WARNING
        )
        val alerts = current.activeAlerts.filterNot { it.code == "BATTERY_LOW" } + alert
        _telemetry.value = current.copy(
            batteryPercentage = percentage,
            activeAlerts = alerts
        )
        logEventAsync("ALERT", alert.title, alert.message, "WARNING")
    }

    override fun triggerObstacleIncident() {
        val current = _telemetry.value
        val alert = AlertItem(
            id = UUID.randomUUID().toString(),
            code = "OBSTACLE_BLOCKED",
            title = "Obstáculo Detectado en Ruta",
            message = "Trayectoria bloqueada por más de 10s. Requiere decisión del operador.",
            severity = AlertSeverity.WARNING
        )
        _telemetry.value = current.copy(
            operationalState = OperationalState.PAUSED,
            isMoving = false,
            speedMps = 0f,
            activeAlerts = current.activeAlerts + alert
        )
        logEventAsync("SAFETY", alert.title, alert.message, "WARNING")
    }

    override fun triggerServiceAlert(gasPpm: Double, tempCelsius: Double) {
        val current = _telemetry.value
        val alert = AlertItem(
            id = UUID.randomUUID().toString(),
            code = "GAS_LEAK",
            title = "Peligro: Fuga de Gas / Sobrecalentamiento",
            message = "Lectura anómala en tablero eléctrico (${gasPpm} ppm, ${tempCelsius}°C). Freno preventivo.",
            severity = AlertSeverity.CRITICAL
        )
        _telemetry.value = current.copy(
            operationalState = OperationalState.PAUSED,
            isMoving = false,
            speedMps = 0f,
            smokeGasPpm = gasPpm,
            ambientTemperatureCelsius = tempCelsius,
            activeAlerts = current.activeAlerts + alert
        )
        logEventAsync("SAFETY", alert.title, alert.message, "CRITICAL")
    }

    override fun toggleForceRejectCommands(reject: Boolean) {
        isForcedRejection = reject
    }

    override fun resetSimulation() {
        isForcedOffline = false
        isForcedRejection = false
        _telemetry.value = TelemetryData(
            robotId = "Unitree-G1-Alpha",
            operationalState = OperationalState.STANDBY,
            zoneName = baseStation.name,
            posX = baseStation.x,
            posY = baseStation.y,
            totalWaypoints = waypoints.size,
            isCharging = true
        )
        _lastCommand.value = null
        logEventAsync("SYSTEM", "Simulación Reiniciada", "Valores reestablecidos a valores de fábrica", "INFO")
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
                    totalWaypoints = waypoints.size,
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

    private data class Waypoint(val name: String, val x: Float, val y: Float)
}
