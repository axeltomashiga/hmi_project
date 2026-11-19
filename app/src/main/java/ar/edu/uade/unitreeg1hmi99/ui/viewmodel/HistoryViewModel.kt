package ar.edu.uade.unitreeg1hmi99.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ar.edu.uade.unitreeg1hmi99.domain.model.EventLogRecord
import ar.edu.uade.unitreeg1hmi99.domain.model.MissionRecord
import ar.edu.uade.unitreeg1hmi99.domain.repository.RobotRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HistoryUiState(
    val missions: List<MissionRecord> = emptyList(),
    val eventLogs: List<EventLogRecord> = emptyList(),
    val isLoading: Boolean = false
)

class HistoryViewModel(
    private val repository: RobotRepository
) : ViewModel() {

    val uiState: StateFlow<HistoryUiState> = combine(
        repository.getMissionHistory(),
        repository.getEventLogs()
    ) { missions, logs ->
        HistoryUiState(
            missions = missions,
            eventLogs = logs,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HistoryUiState(isLoading = true)
    )

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    class Factory(private val repository: RobotRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
                return HistoryViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
