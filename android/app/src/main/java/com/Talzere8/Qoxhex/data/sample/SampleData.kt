package com.Talzere8.Qoxhex.data.sample

import com.Talzere8.Qoxhex.core.config.GameConfig
import com.Talzere8.Qoxhex.domain.model.MissionDefinition
import com.Talzere8.Qoxhex.domain.model.Stage
import com.Talzere8.Qoxhex.domain.model.StampDefinition

object SampleData {

    val stages: List<Stage> = listOf(
        Stage(index = 1, tapedShelves = 1, quota = 6, timerMs = GameConfig.PARCEL_TIMEOUT_MS),
        Stage(index = 2, tapedShelves = 2, quota = 6, timerMs = GameConfig.PARCEL_TIMEOUT_MS),
        Stage(index = 3, tapedShelves = 3, quota = 7, timerMs = GameConfig.PARCEL_TIMEOUT_MS),
        Stage(index = 4, tapedShelves = 4, quota = 7, timerMs = GameConfig.PARCEL_TIMEOUT_MS),
        Stage(index = 5, tapedShelves = 5, quota = 8, timerMs = GameConfig.PARCEL_TIMEOUT_MS),
        Stage(index = 6, tapedShelves = 6, quota = 8, timerMs = GameConfig.PARCEL_TIMEOUT_MS)
    )

    val stamps: List<StampDefinition> = (1..GameConfig.STAMP_COUNT).map { id ->
        StampDefinition(id = id, spriteSlot = 1 + (id - 1) % 4)
    }

    val missions: List<MissionDefinition> = listOf(
        MissionDefinition(id = 1, target = 15),
        MissionDefinition(id = 2, target = 1),
        MissionDefinition(id = 3, target = 1),
        MissionDefinition(id = 4, target = 1),
        MissionDefinition(id = 5, target = 1),
        MissionDefinition(id = 6, target = 5)
    )

    fun stage(index: Int): Stage =
        stages.firstOrNull { it.index == index } ?: stages.first()

    fun mission(id: Int): MissionDefinition =
        missions.firstOrNull { it.id == id } ?: missions.first()

    fun stamp(id: Int): StampDefinition =
        stamps.firstOrNull { it.id == id } ?: stamps.first()
}
