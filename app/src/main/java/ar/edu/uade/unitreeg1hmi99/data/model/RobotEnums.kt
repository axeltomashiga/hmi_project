package ar.edu.uade.unitreeg1hmi99.data.model

import androidx.annotation.StringRes
import ar.edu.uade.unitreeg1hmi99.R

enum class OperationalState(@get:StringRes val displayNameRes: Int) {
    STANDBY(R.string.state_standby),
    PATROLLING(R.string.state_patrolling),
    PAUSED(R.string.state_paused),
    RETURNING_HOME(R.string.state_returning_home),
    EMERGENCY_STOP(R.string.state_emergency_stop),
    FAULT(R.string.state_fault)
}

enum class ConnectionState(@get:StringRes val displayNameRes: Int) {
    ONLINE(R.string.connection_online),
    DEGRADED(R.string.connection_degraded),
    OFFLINE(R.string.connection_offline)
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

enum class CommandType(@get:StringRes val labelRes: Int) {
    START_MISSION(R.string.cmd_start_mission),
    PAUSE_MISSION(R.string.cmd_pause_mission),
    RESUME_MISSION(R.string.cmd_resume_mission),
    CANCEL_MISSION(R.string.cmd_cancel_mission),
    RETURN_TO_SAFE_POINT(R.string.cmd_return_safe_point),
    OPERATIONAL_STOP(R.string.cmd_operational_stop),
    ACKNOWLEDGE_ALERT(R.string.cmd_acknowledge_alert)
}

enum class CommandStatus(@get:StringRes val labelRes: Int) {
    PENDING(R.string.cmd_status_pending),
    CONFIRMED(R.string.cmd_status_confirmed),
    REJECTED(R.string.cmd_status_rejected),
    NO_RESPONSE(R.string.cmd_status_no_response)
}

data class CommandRecord(
    val id: String,
    val type: CommandType,
    val status: CommandStatus,
    val timestamp: Long = System.currentTimeMillis(),
    val message: String = ""
)
