package com.Talzere8.Qoxhex.domain.usecase

import com.Talzere8.Qoxhex.core.config.GameConfig
import com.Talzere8.Qoxhex.domain.model.ShiftResult
import com.Talzere8.Qoxhex.domain.model.ShiftTally
import com.Talzere8.Qoxhex.domain.model.ShiftVerdict

class EvaluateShiftUseCase {

    operator fun invoke(
        tally: ShiftTally,
        bestScore: Int,
        stagesUnlocked: Int,
        unlockedStamps: Set<Int>
    ): ShiftResult {
        val perfect = tally.strikes == 0 && tally.verdict == ShiftVerdict.COMPLETE
        val finalScore = if (perfect) tally.score + GameConfig.PERFECT_SHIFT_BONUS else tally.score
        val cleared = tally.verdict == ShiftVerdict.COMPLETE && tally.strikes <= 1
        val unlocked = if (cleared && tally.stageIndex == stagesUnlocked &&
            stagesUnlocked < GameConfig.STAGE_COUNT
        ) {
            stagesUnlocked + 1
        } else {
            -1
        }
        val stamp = pickStamp(tally, finalScore, perfect, unlockedStamps)
        return ShiftResult(
            stageIndex = tally.stageIndex,
            verdict = tally.verdict,
            score = finalScore,
            delivered = tally.delivered,
            quota = tally.quota,
            bestCombo = tally.bestCombo,
            tidySeals = tally.tidySeals,
            expressUsed = tally.expressUsed,
            perfect = perfect,
            newBest = finalScore > bestScore,
            stampEarned = stamp,
            stageUnlocked = unlocked
        )
    }

    private fun pickStamp(
        tally: ShiftTally,
        finalScore: Int,
        perfect: Boolean,
        unlockedStamps: Set<Int>
    ): Int {
        val cleared = tally.verdict == ShiftVerdict.COMPLETE
        val earned = listOf(
            1 to (tally.delivered >= 1),
            2 to perfect,
            3 to (tally.bestCombo >= 3f),
            4 to (cleared && tally.stageIndex >= 2),
            5 to (tally.delivered >= 5),
            6 to (finalScore >= 1500),
            7 to (finalScore >= 3000),
            8 to (tally.expressUsed >= 1),
            9 to (tally.tidySeals >= 3),
            10 to (tally.expressUsed >= 2),
            11 to (cleared && tally.stageIndex >= 3),
            12 to (cleared && tally.stageIndex >= 6)
        )
        for (candidate in earned) {
            if (candidate.second && !unlockedStamps.contains(candidate.first)) return candidate.first
        }
        return -1
    }
}
