package com.Talzere8.Qoxhex.domain.model

enum class RouteColour(val code: String) {
    ORANGE("O"),
    COBALT("C"),
    PAPER("W"),
    GRAPHITE("G")
}

enum class Town {
    RIVERSIDE,
    OAK_PARK,
    NORTH_DOCK,
    HILLSIDE
}

enum class ShiftVerdict {
    COMPLETE,
    JAMMED,
    BELL
}

data class Parcel(
    val number: Int,
    val colour: RouteColour,
    val town: Town,
    val rush: Boolean
)

data class Bay(
    val index: Int,
    val colour: RouteColour,
    val town: Town
)

data class Stage(
    val index: Int,
    val tapedShelves: Int,
    val quota: Int,
    val timerMs: Long
)

data class ShiftLayout(
    val seed: Long,
    val stageIndex: Int,
    val bays: List<Bay>,
    val blocked: Set<Int>,
    val parcels: List<Parcel>
)

data class StampDefinition(
    val id: Int,
    val spriteSlot: Int
)

data class MissionDefinition(
    val id: Int,
    val target: Int
)

data class MissionSlot(
    val position: Int,
    val missionId: Int,
    val progress: Int,
    val target: Int
) {
    val complete: Boolean get() = progress >= target
}

data class ShiftTally(
    val stageIndex: Int,
    val verdict: ShiftVerdict,
    val score: Int,
    val delivered: Int,
    val quota: Int,
    val strikes: Int,
    val bestCombo: Float,
    val tidySeals: Int,
    val expressUsed: Int,
    val rushDelivered: Int
)

data class ShiftResult(
    val stageIndex: Int,
    val verdict: ShiftVerdict,
    val score: Int,
    val delivered: Int,
    val quota: Int,
    val bestCombo: Float,
    val tidySeals: Int,
    val expressUsed: Int,
    val perfect: Boolean,
    val newBest: Boolean,
    val stampEarned: Int,
    val stageUnlocked: Int
)

object Lattice {
    fun branchKey(row: Int, index: Int, right: Boolean): Int =
        row * 100 + index * 10 + if (right) 1 else 0

    fun allBranches(): List<Int> {
        val keys = mutableListOf<Int>()
        for (row in 0 until 4) {
            for (index in 0..row) {
                keys.add(branchKey(row, index, false))
                keys.add(branchKey(row, index, true))
            }
        }
        return keys
    }
}
