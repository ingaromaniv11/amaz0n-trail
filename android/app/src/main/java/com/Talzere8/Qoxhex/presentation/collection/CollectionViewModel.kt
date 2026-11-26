package com.Talzere8.Qoxhex.presentation.collection

import androidx.lifecycle.ViewModel
import com.Talzere8.Qoxhex.core.config.GameConfig
import com.Talzere8.Qoxhex.data.sample.SampleData
import com.Talzere8.Qoxhex.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class StampTile(
    val id: Int,
    val spriteSlot: Int,
    val unlocked: Boolean
)

data class CollectionUiState(
    val tiles: List<StampTile> = emptyList(),
    val unlockedCount: Int = 0,
    val total: Int = GameConfig.STAMP_COUNT
)

class CollectionViewModel(
    private val collectionRepository: CollectionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CollectionUiState())
    val state: StateFlow<CollectionUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val unlocked = collectionRepository.unlocked()
        _state.update {
            it.copy(
                tiles = SampleData.stamps.map { definition ->
                    StampTile(
                        id = definition.id,
                        spriteSlot = definition.spriteSlot,
                        unlocked = unlocked.contains(definition.id)
                    )
                },
                unlockedCount = unlocked.size,
                total = GameConfig.STAMP_COUNT
            )
        }
    }
}
