package com.Talzere8.Qoxhex.core.navigation

import com.Talzere8.Qoxhex.domain.model.ShiftResult

sealed interface Screen {
    data object Splash : Screen
    data object Menu : Screen
    data class Game(val runId: Long) : Screen
    data class Result(val result: ShiftResult) : Screen
    data object LevelMap : Screen
    data object Tutorial : Screen
    data object Collection : Screen
    data object Missions : Screen
    data object Settings : Screen
}
