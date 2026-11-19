package ar.edu.uade.unitreeg1hmi99.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ar.edu.uade.unitreeg1hmi99.domain.model.BuildingMap
import ar.edu.uade.unitreeg1hmi99.domain.model.CommandRecord
import ar.edu.uade.unitreeg1hmi99.domain.model.CommandType
import ar.edu.uade.unitreeg1hmi99.domain.model.FloorMap
import ar.edu.uade.unitreeg1hmi99.domain.model.MapWaypoint
import ar.edu.uade.unitreeg1hmi99.domain.model.OperationalState
import ar.edu.uade.unitreeg1hmi99.domain.model.TelemetryData
import ar.edu.uade.unitreeg1hmi99.domain.repository.RobotRepository
import ar.edu.uade.unitreeg1hmi99.domain.usecase.CommandAvailability
import ar.edu.uade.unitreeg1hmi99.domain.usecase.ValidateRobotCommandUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MissionTemplate(
    val id: String,
    val name: String,
    val description: String,
    val waypointsCount: Int,
    val estimatedDuration: String
)

private val DEFAULT_MISSIONS = listOf(
    MissionTemplate("MIS-A", "Ronda Integral del Edificio", "Inspección completa de pasillos, laboratorios y tableros", 8, "12 min"),
    MissionTemplate("MIS-B", "Inspección Técnica de Tableros", "Monitoreo prioritario de salas de servidores y tableros eléctricos", 5, "8 min"),
    MissionTemplate("MIS-C", "Patrullaje Perimetral y Accesos", "Supervisión de salidas de emergencia y accesos principales", 4, "6 min")
)

data class RobotUiState(
    val telemetry: TelemetryData = TelemetryData(),
    val lastCommand: CommandRecord? = null,
    val isForcedOffline: Boolean = false,
    val isForcedRejection: Boolean = false,
    val selectedRobot: String = "Unitree G1 (Humanoide Bípedo)",
    val showEmergencyConfirmDialog: Boolean = false,
    val showCancelConfirmDialog: Boolean = false,
    val availableMissions: List<MissionTemplate> = DEFAULT_MISSIONS,
    val selectedMission: MissionTemplate = DEFAULT_MISSIONS.first(),
    val commandAvailability: CommandAvailability = CommandAvailability(),
    // Datos para el mapa 2D
    val floorMap: FloorMap = BuildingMap.default,
    val plannedRoute: List<MapWaypoint> = emptyList(),
    val routeReached: Int = 0
)

class RobotViewModel(
    private val repository: RobotRepository,
    private val validateCommandUseCase: ValidateRobotCommandUseCase = ValidateRobotCommandUseCase()
) : ViewModel() {

    private val _showEmergencyDialog = MutableStateFlow(false)
    private val _showCancelDialog = MutableStateFlow(false)
    private val _selectedMission = MutableStateFlow(
        DEFAULT_MISSIONS.firstOrNull { it.id == repository.telemetry.value.missionId } ?: DEFAULT_MISSIONS.first()
    )

    private fun buildUiState(
        telemetry: TelemetryData,
        lastCmd: CommandRecord?,
        offline: Boolean,
        reject: Boolean,
        mission: MissionTemplate,
        emergencyDlg: Boolean,
        cancelDlg: Boolean
    ): RobotUiState {
        // Ruta mostrada en el mapa: la misión activa (o recién finalizada) o, si no hay, la seleccionada
        val showActiveMission = telemetry.missionId != null &&
            (telemetry.operationalState != OperationalState.STANDBY || telemetry.justCompletedMissionName != null)
        val routeMissionId = if (showActiveMission) telemetry.missionId else mission.id
        return RobotUiState(
            floorMap = repository.floorMap,
            plannedRoute = repository.floorMap.routeFor(routeMissionId),
            routeReached = if (showActiveMission) telemetry.waypointsReached else 0,
            telemetry = telemetry,
            lastCommand = lastCmd,
            isForcedOffline = offline,
            isForcedRejection = reject,
            selectedMission = mission,
            commandAvailability = validateCommandUseCase.evaluateAvailability(
                telemetry = telemetry,
                baseStation = repository.floorMap.base
            ),
            showEmergencyConfirmDialog = emergencyDlg,
            showCancelConfirmDialog = cancelDlg
        )
    }

    val uiState: StateFlow<RobotUiState> = combine(
        repository.telemetry,
        repository.lastCommand,
        repository.isForcedOffline,
        repository.isForcedRejection,
        _selectedMission
    ) { telemetry, lastCmd, offline, reject, mission ->
        buildUiState(
            telemetry = telemetry,
            lastCmd = lastCmd,
            offline = offline,
            reject = reject,
            mission = mission,
            emergencyDlg = _showEmergencyDialog.value,
            cancelDlg = _showCancelDialog.value
        )
    }.combine(_showEmergencyDialog) { state, emergencyDlg ->
        state.copy(showEmergencyConfirmDialog = emergencyDlg)
    }.combine(_showCancelDialog) { state, cancelDlg ->
        state.copy(showCancelConfirmDialog = cancelDlg)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = buildUiState(
            telemetry = repository.telemetry.value,
            lastCmd = repository.lastCommand.value,
            offline = repository.isForcedOffline.value,
            reject = repository.isForcedRejection.value,
            mission = _selectedMission.value,
            emergencyDlg = _showEmergencyDialog.value,
            cancelDlg = _showCancelDialog.value
        )
    )

    fun selectMission(mission: MissionTemplate) {
        _selectedMission.value = mission
    }

    fun sendCommand(type: CommandType) {
        viewModelScope.launch {
            val params = if (type == CommandType.START_MISSION) {
                mapOf("missionName" to _selectedMission.value.name, "missionId" to _selectedMission.value.id)
            } else {
                emptyMap()
            }
            repository.sendCommand(type, params)
        }
    }

    fun acknowledgeAlert(alertId: String) {
        viewModelScope.launch {
            repository.acknowledgeAlert(alertId)
        }
    }

    fun acknowledgeAllAlerts() {
        viewModelScope.launch {
            val alerts = uiState.value.telemetry.activeAlerts
            alerts.forEach { alert ->
                repository.acknowledgeAlert(alert.id)
            }
        }
    }

    fun dismissMissionCompleted() {
        repository.dismissMissionCompleted()
    }

    fun setEmergencyDialogVisible(visible: Boolean) {
        _showEmergencyDialog.value = visible
    }

    fun setCancelDialogVisible(visible: Boolean) {
        _showCancelDialog.value = visible
    }

    fun saveSimulationState(sync: Boolean = true) {
        repository.saveStateNow(sync = sync)
    }

    // --- Métodos de demostración para los 5 Escenarios del TP ---

    /** Escenario 1: Misión Normal */
    fun runNormalMissionScenario() {
        sendCommand(CommandType.START_MISSION)
    }

    /** Escenario 2: Batería Baja */
    fun triggerLowBatteryScenario() {
        repository.triggerLowBattery(12)
    }

    /** Escenario 3: Forzar Desconexión y Reconexión */
    fun toggleDisconnectionScenario() {
        repository.toggleDisconnection(!repository.isForcedOffline.value)
    }

    /** Escenario 4: Incidente de Obstáculo / Bloqueo */
    fun triggerObstacleScenario() {
        repository.triggerObstacleIncident()
    }

    /** Escenario 5: Escenario Propio (Fuga de Gas y Sobrecalentamiento) */
    fun triggerGasOverheatScenario() {
        repository.triggerServiceAlert(gasPpm = 96.5, tempCelsius = 54.0)
    }

    /** Simulación de rechazo de órdenes */
    fun toggleCommandRejectionScenario() {
        repository.toggleForceRejectCommands(!repository.isForcedRejection.value)
    }

    fun resetSimulation() {
        repository.resetSimulation()
    }

    class Factory(private val repository: RobotRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RobotViewModel::class.java)) {
                return RobotViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
