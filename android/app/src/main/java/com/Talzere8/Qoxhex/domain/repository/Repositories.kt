package com.Talzere8.Qoxhex.domain.repository

import com.Talzere8.Qoxhex.domain.model.MissionSlot

interface ProgressRepository {
    fun bestScore(): Int
    fun stagesUnlocked(): Int
    fun selectedStage(): Int
    fun totalDelivered(): Int
    fun perfectShifts(): Int
    fun stageBest(stageIndex: Int): Int
    fun selectStage(stageIndex: Int)
    fun unlockStage(stageIndex: Int)
    fun recordShift(stageIndex: Int, score: Int, delivered: Int, perfect: Boolean)
    fun clear()
}

interface CollectionRepository {
    fun unlocked(): Set<Int>
    fun unlock(stampId: Int)
    fun clear()
}

interface MissionRepository {
    fun slots(): List<MissionSlot>
    fun advance(delivered: Int, bestCombo: Float, perfect: Boolean, expressUsed: Int, noStrikeClear: Boolean, rushDelivered: Int)
    fun claim(position: Int)
    fun clear()
}

interface SettingsRepository {
    fun relaxedTimer(): Boolean
    fun colourCodes(): Boolean
    fun routeHints(): Boolean
    fun setRelaxedTimer(value: Boolean)
    fun setColourCodes(value: Boolean)
    fun setRouteHints(value: Boolean)
}
