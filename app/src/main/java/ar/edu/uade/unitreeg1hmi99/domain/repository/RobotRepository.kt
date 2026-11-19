package ar.edu.uade.unitreeg1hmi99.domain.repository

import ar.edu.uade.unitreeg1hmi99.domain.model.CommandRecord
import ar.edu.uade.unitreeg1hmi99.domain.model.CommandType
import ar.edu.uade.unitreeg1hmi99.domain.model.EventLogRecord
import ar.edu.uade.unitreeg1hmi99.domain.model.FloorMap
import ar.edu.uade.unitreeg1hmi99.domain.model.MissionRecord
import ar.edu.uade.unitreeg1hmi99.domain.model.TelemetryData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface RobotRepository {
    /** Plano 2D del entorno, waypoints y rutas de cada misión */
    val floorMap: FloorMap

    /** Flujo continuo de telemetría dinámica en tiempo real */
    val telemetry: StateFlow<TelemetryData>

    /** Registro de la última orden emitida y su estado (PENDING, CONFIRMED, REJECTED, NO_RESPONSE) */
    val lastCommand: StateFlow<CommandRecord?>

    /** Banderas observables de simulación de fallas para mantener sincronizada la UI */
    val isForcedOffline: StateFlow<Boolean>
    val isForcedRejection: StateFlow<Boolean>

    /** Emisión de comandos con validación de condiciones de habilitación y simulación de respuesta */
    suspend fun sendCommand(type: CommandType, parameters: Map<String, String> = emptyMap()): CommandRecord

    /** Reconocimiento de alertas por parte del operador */
    suspend fun acknowledgeAlert(alertId: String)

    /** Desestimar modal de misión completada tras tomar una decisión */
    fun dismissMissionCompleted()

    /** Flujos de auditoría expuestos como modelos de dominio puros (desacoplados de Room) */
    fun getMissionHistory(): Flow<List<MissionRecord>>
    fun getEventLogs(): Flow<List<EventLogRecord>>
    suspend fun clearHistory()

    // --- Disparadores para demostración de los 5 escenarios de prueba de la consigna ---
    fun toggleDisconnection(forceOffline: Boolean)
    fun triggerLowBattery(percentage: Int = 12)
    fun triggerObstacleIncident()
    fun triggerServiceAlert(gasPpm: Double = 98.0, tempCelsius: Double = 52.0)
    fun toggleForceRejectCommands(reject: Boolean)
    fun resetSimulation()

    /** Persiste de inmediato el estado actual de la simulación (por ej. al pasar a segundo plano). */
    fun saveStateNow(sync: Boolean = true) {}
}
