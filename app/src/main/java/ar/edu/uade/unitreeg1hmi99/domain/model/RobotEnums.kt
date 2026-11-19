package ar.edu.uade.unitreeg1hmi99.domain.model

enum class OperationalState {
    STANDBY,
    PATROLLING,
    PAUSED,
    RETURNING_HOME,
    EMERGENCY_STOP,
    FAULT
}

enum class ConnectionState {
    ONLINE,
    DEGRADED,
    OFFLINE
}

enum class AlertSeverity {
    INFO,
    WARNING,
    CRITICAL
}

data class AlertItem(
    val id: String,
    val code: String,
    val title: String,
    val message: String,
    val severity: AlertSeverity,
    val timestamp: Long = System.currentTimeMillis(),
    val acknowledged: Boolean = false
)

enum class CommandType {
    START_MISSION,
    PAUSE_MISSION,
    RESUME_MISSION,
    CANCEL_MISSION,
    RETURN_TO_SAFE_POINT,
    OPERATIONAL_STOP,
    ACKNOWLEDGE_ALERT
}

enum class CommandStatus {
    PENDING,
    CONFIRMED,
    REJECTED,
    NO_RESPONSE
}

data class CommandRecord(
    val id: String,
    val type: CommandType,
    val status: CommandStatus,
    val timestamp: Long = System.currentTimeMillis(),
    val message: String = ""
)
