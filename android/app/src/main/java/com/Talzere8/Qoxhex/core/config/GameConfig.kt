package com.Talzere8.Qoxhex.core.config

object GameConfig {
    const val LOADER_DURATION_MS = 8000L
    const val PARCEL_TIMEOUT_MS = 9000L
    const val RELAXED_TIMEOUT_MS = 12000L
    const val RUSH_TIMEOUT_MS = 6000L
    const val ROUND_HARD_CAP_MS = 40000L
    const val STRIKES_ALLOWED = 3
    const val ROWS = 4
    const val COLS = 5
    const val BRANCHES = 20
    const val EXPRESS_STREAK = 3
    const val PERFECT_SHIFT_BONUS = 250
    const val BASE_DELIVERY_SCORE = 100
    const val TIME_BONUS_PER_SECOND = 10
    const val RUSH_PARCEL_NUMBER = 4
    const val GENERATOR_ATTEMPTS = 20
    const val TICK_MS = 100L
    const val MOVE_LOCK_MS = 420L
    const val DELIVERED_HOLD_MS = 700L
    const val MISROUTE_HOLD_MS = 900L
    const val FINISH_HOLD_MS = 1200L
    const val TAPED_NOTICE_MS = 900L
    const val STAGE_COUNT = 6
    const val STAMP_COUNT = 12
    const val MISSION_SLOTS = 3
    const val MISSION_POOL = 6
    val COMBO_STEPS = listOf(1.0f, 1.5f, 2.0f, 3.0f)
}
