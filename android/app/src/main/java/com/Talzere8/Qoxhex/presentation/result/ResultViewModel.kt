package com.Talzere8.Qoxhex.presentation.result

import androidx.lifecycle.ViewModel
import com.Talzere8.Qoxhex.core.config.GameConfig
import com.Talzere8.Qoxhex.domain.repository.CollectionRepository
import com.Talzere8.Qoxhex.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ResultUiState(
    val bestScore: Int = 0,
    val stampsCollected: Int = 0,
    val stampsTotal: Int = GameConfig.STAMP_COUNT
)

class ResultViewModel(
    private val progressRepository: ProgressRepository,
    private val collectionRepository: CollectionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ResultUiState())
    val state: StateFlow<ResultUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        _state.update {
            it.copy(
                bestScore = progressRepository.bestScore(),
                stampsCollected = collectionRepository.unlocked().size,
                stampsTotal = GameConfig.STAMP_COUNT
            )
        }
    }
}
