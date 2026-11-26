package com.Talzere8.Qoxhex.presentation.menu

import androidx.lifecycle.ViewModel
import com.Talzere8.Qoxhex.core.config.GameConfig
import com.Talzere8.Qoxhex.domain.repository.CollectionRepository
import com.Talzere8.Qoxhex.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MenuViewModel(
    private val progressRepository: ProgressRepository,
    private val collectionRepository: CollectionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MenuUiState())
    val state: StateFlow<MenuUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        _state.update {
            it.copy(
                bestScore = progressRepository.bestScore(),
                selectedStage = progressRepository.selectedStage(),
                stagesUnlocked = progressRepository.stagesUnlocked(),
                totalDelivered = progressRepository.totalDelivered(),
                stampsCollected = collectionRepository.unlocked().size,
                stampsTotal = GameConfig.STAMP_COUNT
            )
        }
    }
}
