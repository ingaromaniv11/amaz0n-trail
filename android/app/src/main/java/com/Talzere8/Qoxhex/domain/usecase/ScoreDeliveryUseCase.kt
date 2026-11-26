package com.Talzere8.Qoxhex.domain.usecase

import com.Talzere8.Qoxhex.core.config.GameConfig

class ScoreDeliveryUseCase {

    operator fun invoke(
        secondsRemaining: Int,
        combo: Float,
        express: Boolean,
        rush: Boolean
    ): Int {
        val timeBonus = secondsRemaining.coerceAtLeast(0) * GameConfig.TIME_BONUS_PER_SECOND
        val subtotal = ((GameConfig.BASE_DELIVERY_SCORE + timeBonus) * combo).toInt()
        val doubled = express || rush
        return if (doubled) subtotal * 2 else subtotal
    }

    fun nextCombo(current: Float): Float {
        val steps = GameConfig.COMBO_STEPS
        val position = steps.indexOfFirst { it >= current }
        val index = if (position < 0) steps.size - 1 else position + 1
        return steps[index.coerceAtMost(steps.size - 1)]
    }
}
