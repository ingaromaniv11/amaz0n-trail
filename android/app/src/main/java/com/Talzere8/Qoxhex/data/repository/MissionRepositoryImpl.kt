package com.Talzere8.Qoxhex.data.repository

import com.Talzere8.Qoxhex.core.config.GameConfig
import com.Talzere8.Qoxhex.data.local.PreferencesStore
import com.Talzere8.Qoxhex.data.sample.SampleData
import com.Talzere8.Qoxhex.domain.model.MissionSlot
import com.Talzere8.Qoxhex.domain.repository.MissionRepository

class MissionRepositoryImpl(private val store: PreferencesStore) : MissionRepository {

    override fun slots(): List<MissionSlot> = (0 until GameConfig.MISSION_SLOTS).map { position ->
        val missionId = store.readInt(PreferencesStore.KEY_MISSION_SLOT + position, position + 1)
        val definition = SampleData.mission(missionId)
        MissionSlot(
            position = position,
            missionId = definition.id,
            progress = store.readInt(PreferencesStore.KEY_MISSION_PROGRESS + position, 0)
                .coerceAtMost(definition.target),
            target = definition.target
        )
    }

    override fun advance(
        delivered: Int,
        bestCombo: Float,
        perfect: Boolean,
        expressUsed: Int,
        noStrikeClear: Boolean,
        rushDelivered: Int
    ) {
        for (slot in slots()) {
            val gain = when (slot.missionId) {
                1 -> delivered
                2 -> if (bestCombo >= 3f) 1 else 0
                3 -> if (perfect) 1 else 0
                4 -> if (expressUsed > 0) 1 else 0
                5 -> if (noStrikeClear) 1 else 0
                else -> rushDelivered
            }
            if (gain <= 0) continue
            val updated = (slot.progress + gain).coerceAtMost(slot.target)
            store.writeInt(PreferencesStore.KEY_MISSION_PROGRESS + slot.position, updated)
        }
    }

    override fun claim(position: Int) {
        val current = slots()
        val slot = current.firstOrNull { it.position == position } ?: return
        if (!slot.complete) return
        val active = current.map { it.missionId }.toMutableSet()
        val replacement = (1..GameConfig.MISSION_POOL).firstOrNull { !active.contains(it) }
            ?: ((slot.missionId % GameConfig.MISSION_POOL) + 1)
        store.writeInt(PreferencesStore.KEY_MISSION_SLOT + position, replacement)
        store.writeInt(PreferencesStore.KEY_MISSION_PROGRESS + position, 0)
    }

    override fun clear() {
        val keys = mutableListOf<String>()
        for (position in 0 until GameConfig.MISSION_SLOTS) {
            keys.add(PreferencesStore.KEY_MISSION_SLOT + position)
            keys.add(PreferencesStore.KEY_MISSION_PROGRESS + position)
        }
        store.removeKeys(keys)
    }
}
