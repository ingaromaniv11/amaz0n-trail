package com.Talzere8.Qoxhex.presentation.settings

import androidx.lifecycle.ViewModel
import com.Talzere8.Qoxhex.domain.repository.CollectionRepository
import com.Talzere8.Qoxhex.domain.repository.MissionRepository
import com.Talzere8.Qoxhex.domain.repository.ProgressRepository
import com.Talzere8.Qoxhex.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SettingsUiState(
    val relaxedTimer: Boolean = false,
    val colourCodes: Boolean = false,
    val routeHints: Boolean = true,
    val confirmVisible: Boolean = false,
    val resetDone: Boolean = false
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val progressRepository: ProgressRepository,
    private val collectionRepository: CollectionRepository,
    private val missionRepository: MissionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        _state.update {
            it.copy(
                relaxedTimer = settingsRepository.relaxedTimer(),
                colourCodes = settingsRepository.colourCodes(),
                routeHints = settingsRepository.routeHints()
            )
        }
    }

    fun setRelaxedTimer(value: Boolean) {
        settingsRepository.setRelaxedTimer(value)
        _state.update { it.copy(relaxedTimer = value) }
    }

    fun setColourCodes(value: Boolean) {
        settingsRepository.setColourCodes(value)
        _state.update { it.copy(colourCodes = value) }
    }

    fun setRouteHints(value: Boolean) {
        settingsRepository.setRouteHints(value)
        _state.update { it.copy(routeHints = value) }
    }

    fun askReset() {
        _state.update { it.copy(confirmVisible = true) }
    }

    fun cancelReset() {
        _state.update { it.copy(confirmVisible = false) }
    }

    fun confirmReset() {
        progressRepository.clear()
        collectionRepository.clear()
        missionRepository.clear()
        _state.update { it.copy(confirmVisible = false, resetDone = true) }
    }
}
