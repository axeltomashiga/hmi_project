package ar.edu.uade.unitreeg1hmi99.ui.components

import androidx.annotation.StringRes
import ar.edu.uade.unitreeg1hmi99.R
import ar.edu.uade.unitreeg1hmi99.domain.model.CommandStatus
import ar.edu.uade.unitreeg1hmi99.domain.model.CommandType
import ar.edu.uade.unitreeg1hmi99.domain.model.ConnectionState
import ar.edu.uade.unitreeg1hmi99.domain.model.OperationalState

@get:StringRes
val OperationalState.displayNameRes: Int
    get() = when (this) {
        OperationalState.STANDBY -> R.string.state_standby
        OperationalState.PATROLLING -> R.string.state_patrolling
        OperationalState.PAUSED -> R.string.state_paused
        OperationalState.RETURNING_HOME -> R.string.state_returning_home
        OperationalState.EMERGENCY_STOP -> R.string.state_emergency_stop
        OperationalState.FAULT -> R.string.state_fault
    }

@get:StringRes
val ConnectionState.displayNameRes: Int
    get() = when (this) {
        ConnectionState.ONLINE -> R.string.connection_online
        ConnectionState.DEGRADED -> R.string.connection_degraded
        ConnectionState.OFFLINE -> R.string.connection_offline
    }

@get:StringRes
val CommandType.labelRes: Int
    get() = when (this) {
        CommandType.START_MISSION -> R.string.cmd_start_mission
        CommandType.PAUSE_MISSION -> R.string.cmd_pause_mission
        CommandType.RESUME_MISSION -> R.string.cmd_resume_mission
        CommandType.CANCEL_MISSION -> R.string.cmd_cancel_mission
        CommandType.RETURN_TO_SAFE_POINT -> R.string.cmd_return_safe_point
        CommandType.OPERATIONAL_STOP -> R.string.cmd_operational_stop
        CommandType.ACKNOWLEDGE_ALERT -> R.string.cmd_acknowledge_alert
    }

@get:StringRes
val CommandStatus.labelRes: Int
    get() = when (this) {
        CommandStatus.PENDING -> R.string.cmd_status_pending
        CommandStatus.CONFIRMED -> R.string.cmd_status_confirmed
        CommandStatus.REJECTED -> R.string.cmd_status_rejected
        CommandStatus.NO_RESPONSE -> R.string.cmd_status_no_response
    }
