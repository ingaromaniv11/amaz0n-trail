package com.Talzere8.Qoxhex.presentation.missions

import androidx.lifecycle.ViewModel
import com.Talzere8.Qoxhex.core.config.GameConfig
import com.Talzere8.Qoxhex.domain.model.MissionSlot
import com.Talzere8.Qoxhex.domain.repository.CollectionRepository
import com.Talzere8.Qoxhex.domain.repository.MissionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MissionsUiState(
    val slots: List<MissionSlot> = emptyList(),
    val claimedStamp: Int = 0
)

class MissionsViewModel(
    private val missionRepository: MissionRepository,
    private val collectionRepository: CollectionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MissionsUiState())
    val state: StateFlow<MissionsUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        _state.update { it.copy(slots = missionRepository.slots()) }
    }

    fun claim(position: Int) {
        val slot = _state.value.slots.firstOrNull { it.position == position } ?: return
        if (!slot.complete) return
        val unlocked = collectionRepository.unlocked()
        val reward = (1..GameConfig.STAMP_COUNT).firstOrNull { !unlocked.contains(it) } ?: 0
        if (reward > 0) collectionRepository.unlock(reward)
        missionRepository.claim(position)
        _state.update {
            it.copy(slots = missionRepository.slots(), claimedStamp = reward)
        }
    }
}
