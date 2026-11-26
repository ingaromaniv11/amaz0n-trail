package com.Talzere8.Qoxhex.presentation.levelmap

import androidx.lifecycle.ViewModel
import com.Talzere8.Qoxhex.data.sample.SampleData
import com.Talzere8.Qoxhex.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class StageNode(
    val index: Int,
    val unlocked: Boolean,
    val selected: Boolean,
    val best: Int
)

data class LevelMapUiState(
    val nodes: List<StageNode> = emptyList(),
    val selectedStage: Int = 1,
    val lockedNotice: Int = 0
)

class LevelMapViewModel(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LevelMapUiState())
    val state: StateFlow<LevelMapUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val unlocked = progressRepository.stagesUnlocked()
        val selected = progressRepository.selectedStage()
        _state.update { current ->
            current.copy(
                nodes = SampleData.stages.map { stage ->
                    StageNode(
                        index = stage.index,
                        unlocked = stage.index <= unlocked,
                        selected = stage.index == selected,
                        best = progressRepository.stageBest(stage.index)
                    )
                },
                selectedStage = selected,
                lockedNotice = 0
            )
        }
    }

    fun select(stageIndex: Int) {
        val node = _state.value.nodes.firstOrNull { it.index == stageIndex } ?: return
        if (!node.unlocked) {
            _state.update { it.copy(lockedNotice = stageIndex - 1) }
            return
        }
        progressRepository.selectStage(stageIndex)
        refresh()
        _state.update { it.copy(selectedStage = stageIndex, lockedNotice = 0) }
    }
}
