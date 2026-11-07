package ar.edu.uade.unitreeg1hmi99.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ar.edu.uade.unitreeg1hmi99.data.model.AlertItem
import ar.edu.uade.unitreeg1hmi99.data.model.CommandRecord
import ar.edu.uade.unitreeg1hmi99.data.model.CommandType
import ar.edu.uade.unitreeg1hmi99.data.model.TelemetryData
import ar.edu.uade.unitreeg1hmi99.data.repository.RobotRepository
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

data class RobotUiState(
    val telemetry: TelemetryData = TelemetryData(),
    val lastCommand: CommandRecord? = null,
    val isForcedOffline: Boolean = false,
    val isForcedRejection: Boolean = false,
    val selectedRobot: String = "Unitree G1 (Humanoide Bípedo)",
    val showEmergencyConfirmDialog: Boolean = false,
    val showCancelConfirmDialog: Boolean = false,
    val availableMissions: List<MissionTemplate> = listOf(
        MissionTemplate("MIS-A", "Ronda Integral del Edificio", "Inspección completa de pasillos, laboratorios y tableros", 8, "12 min"),
        MissionTemplate("MIS-B", "Inspección Técnica de Tableros", "Monitoreo prioritario de salas de servidores y tableros eléctricos", 5, "8 min"),
        MissionTemplate("MIS-C", "Patrullaje Perimetral y Accesos", "Supervisión de salidas de emergencia y accesos principales", 4, "6 min")
    ),
    val selectedMission: MissionTemplate = MissionTemplate("MIS-A", "Ronda Integral del Edificio", "Inspección completa de pasillos, laboratorios y tableros", 8, "12 min")
)

class RobotViewModel(
    private val repository: RobotRepository
) : ViewModel() {

    private val _isForcedOffline = MutableStateFlow(false)
    private val _isForcedRejection = MutableStateFlow(false)
    private val _showEmergencyDialog = MutableStateFlow(false)
    private val _showCancelDialog = MutableStateFlow(false)
    private val _selectedMission = MutableStateFlow(
        MissionTemplate("MIS-A", "Ronda Integral del Edificio", "Inspección completa de pasillos, laboratorios y tableros", 8, "12 min")
    )

    val uiState: StateFlow<RobotUiState> = combine(
        repository.telemetry,
        repository.lastCommand,
        _isForcedOffline,
        _isForcedRejection,
        _selectedMission
    ) { telemetry, lastCmd, offline, reject, mission ->
        RobotUiState(
            telemetry = telemetry,
            lastCommand = lastCmd,
            isForcedOffline = offline,
            isForcedRejection = reject,
            selectedMission = mission,
            showEmergencyConfirmDialog = _showEmergencyDialog.value,
            showCancelConfirmDialog = _showCancelDialog.value
        )
    }.combine(_showEmergencyDialog) { state, emergencyDlg ->
        state.copy(showEmergencyConfirmDialog = emergencyDlg)
    }.combine(_showCancelDialog) { state, cancelDlg ->
        state.copy(showCancelConfirmDialog = cancelDlg)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RobotUiState()
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
        val next = !_isForcedOffline.value
        _isForcedOffline.value = next
        repository.toggleDisconnection(next)
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
        val next = !_isForcedRejection.value
        _isForcedRejection.value = next
        repository.toggleForceRejectCommands(next)
    }

    fun resetSimulation() {
        _isForcedOffline.value = false
        _isForcedRejection.value = false
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
