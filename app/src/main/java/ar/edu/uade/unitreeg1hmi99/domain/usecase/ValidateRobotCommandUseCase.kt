package ar.edu.uade.unitreeg1hmi99.domain.usecase

import ar.edu.uade.unitreeg1hmi99.domain.model.CommandType
import ar.edu.uade.unitreeg1hmi99.domain.model.ConnectionState
import ar.edu.uade.unitreeg1hmi99.domain.model.MapWaypoint
import ar.edu.uade.unitreeg1hmi99.domain.model.OperationalState
import ar.edu.uade.unitreeg1hmi99.domain.model.TelemetryData
import kotlin.math.hypot

/** Estado precalculado de habilitación de comandos para la capa de presentación (MVVM). */
data class CommandAvailability(
    val isOnline: Boolean = true,
    val canStart: Boolean = true,
    val canPause: Boolean = false,
    val canResume: Boolean = false,
    val canCancel: Boolean = false,
    val canReturnSafe: Boolean = false,
    val contextHint: String = "Listo para iniciar misión"
)

/**
 * Caso de uso de dominio que centraliza las reglas de negocio para la habilitación y validación
 * de órdenes operativas enviadas al Unitree G1.
 */
class ValidateRobotCommandUseCase {

    fun validate(
        type: CommandType,
        state: TelemetryData,
        baseStation: MapWaypoint,
        atBaseRadiusM: Float = DEFAULT_AT_BASE_RADIUS_M
    ): Pair<Boolean, String> {
        return when (type) {
            CommandType.START_MISSION -> {
                if (state.operationalState != OperationalState.STANDBY) {
                    Pair(false, "El robot no está en espera (Estado actual: ${state.operationalState.name}).")
                } else if (state.batteryPercentage <= MIN_BATTERY_THRESHOLD) {
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
                val atBase = isAtBase(state, baseStation, atBaseRadiusM)
                if (state.operationalState == OperationalState.STANDBY && atBase) {
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

    fun evaluateAvailability(
        telemetry: TelemetryData,
        baseStation: MapWaypoint,
        atBaseRadiusM: Float = DEFAULT_AT_BASE_RADIUS_M
    ): CommandAvailability {
        val isOnline = telemetry.connectionState != ConnectionState.OFFLINE
        val atBase = isAtBase(telemetry, baseStation, atBaseRadiusM) && telemetry.zoneName.contains("Base")

        val canStart = isOnline &&
            telemetry.operationalState == OperationalState.STANDBY &&
            telemetry.batteryPercentage > MIN_BATTERY_THRESHOLD
        val canPause = isOnline && telemetry.operationalState == OperationalState.PATROLLING
        val canResume = isOnline && telemetry.operationalState == OperationalState.PAUSED
        val canCancel = isOnline && (
            telemetry.operationalState == OperationalState.PATROLLING ||
                telemetry.operationalState == OperationalState.PAUSED
            )
        val canReturnSafe = isOnline && (telemetry.operationalState != OperationalState.STANDBY || !atBase)

        val contextHint = when {
            !isOnline -> "Enlace OFFLINE: órdenes bloqueadas"
            telemetry.batteryPercentage <= MIN_BATTERY_THRESHOLD -> "Batería crítica (≤15%)"
            telemetry.operationalState == OperationalState.STANDBY -> "Listo para iniciar misión"
            telemetry.operationalState == OperationalState.PATROLLING -> "Patrullaje activo"
            telemetry.operationalState == OperationalState.PAUSED -> "Misión en pausa"
            telemetry.operationalState == OperationalState.RETURNING_HOME -> "Retornando a base"
            else -> "Control manual activo"
        }

        return CommandAvailability(
            isOnline = isOnline,
            canStart = canStart,
            canPause = canPause,
            canResume = canResume,
            canCancel = canCancel,
            canReturnSafe = canReturnSafe,
            contextHint = contextHint
        )
    }

    private fun isAtBase(state: TelemetryData, baseStation: MapWaypoint, radiusM: Float): Boolean =
        hypot(state.posX - baseStation.x, state.posY - baseStation.y) < radiusM

    companion object {
        const val MIN_BATTERY_THRESHOLD = 15
        const val DEFAULT_AT_BASE_RADIUS_M = 0.5f
    }
}
