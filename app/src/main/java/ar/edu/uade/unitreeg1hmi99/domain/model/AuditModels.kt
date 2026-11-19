package ar.edu.uade.unitreeg1hmi99.domain.model

/** Modelo de dominio puro para el registro histórico de una misión (desacoplado de Room). */
data class MissionRecord(
    val id: Long = 0,
    val missionId: String,
    val missionName: String,
    val startTime: Long,
    val endTime: Long,
    val status: String, // COMPLETED, CANCELLED, STOPPED, FAILED
    val waypointsCompleted: Int,
    val totalWaypoints: Int,
    val maxGasPpm: Double,
    val maxTempCelsius: Double,
    val incidentsCount: Int,
    val notes: String = ""
)

/** Modelo de dominio puro para un evento de auditoría (desacoplado de Room). */
data class EventLogRecord(
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val eventType: String, // COMMAND, ALERT, STATE_CHANGE, DISCONNECTION, SAFETY
    val title: String,
    val details: String,
    val severity: String = "INFO" // INFO, WARNING, CRITICAL
)
