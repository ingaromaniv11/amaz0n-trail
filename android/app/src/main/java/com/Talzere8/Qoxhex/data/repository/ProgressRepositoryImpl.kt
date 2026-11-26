package com.Talzere8.Qoxhex.data.repository

import com.Talzere8.Qoxhex.core.config.GameConfig
import com.Talzere8.Qoxhex.data.local.PreferencesStore
import com.Talzere8.Qoxhex.domain.repository.ProgressRepository

class ProgressRepositoryImpl(private val store: PreferencesStore) : ProgressRepository {

    override fun bestScore(): Int = store.readInt(PreferencesStore.KEY_BEST_SCORE, 0)

    override fun stagesUnlocked(): Int =
        store.readInt(PreferencesStore.KEY_STAGES_UNLOCKED, 1).coerceIn(1, GameConfig.STAGE_COUNT)

    override fun selectedStage(): Int =
        store.readInt(PreferencesStore.KEY_SELECTED_STAGE, 1).coerceIn(1, stagesUnlocked())

    override fun totalDelivered(): Int = store.readInt(PreferencesStore.KEY_TOTAL_DELIVERED, 0)

    override fun perfectShifts(): Int = store.readInt(PreferencesStore.KEY_PERFECT_SHIFTS, 0)

    override fun stageBest(stageIndex: Int): Int =
        store.readInt(PreferencesStore.KEY_STAGE_BEST + stageIndex, 0)

    override fun selectStage(stageIndex: Int) {
        store.writeInt(PreferencesStore.KEY_SELECTED_STAGE, stageIndex.coerceIn(1, GameConfig.STAGE_COUNT))
    }

    override fun unlockStage(stageIndex: Int) {
        if (stageIndex in 1..GameConfig.STAGE_COUNT && stageIndex > stagesUnlocked()) {
            store.writeInt(PreferencesStore.KEY_STAGES_UNLOCKED, stageIndex)
        }
    }

    override fun recordShift(stageIndex: Int, score: Int, delivered: Int, perfect: Boolean) {
        if (score > bestScore()) store.writeInt(PreferencesStore.KEY_BEST_SCORE, score)
        if (score > stageBest(stageIndex)) {
            store.writeInt(PreferencesStore.KEY_STAGE_BEST + stageIndex, score)
        }
        store.writeInt(PreferencesStore.KEY_TOTAL_DELIVERED, totalDelivered() + delivered)
        if (perfect) store.writeInt(PreferencesStore.KEY_PERFECT_SHIFTS, perfectShifts() + 1)
    }

    override fun clear() {
        val keys = mutableListOf(
            PreferencesStore.KEY_BEST_SCORE,
            PreferencesStore.KEY_STAGES_UNLOCKED,
            PreferencesStore.KEY_SELECTED_STAGE,
            PreferencesStore.KEY_TOTAL_DELIVERED,
            PreferencesStore.KEY_PERFECT_SHIFTS
        )
        for (index in 1..GameConfig.STAGE_COUNT) keys.add(PreferencesStore.KEY_STAGE_BEST + index)
        store.removeKeys(keys)
    }
}
