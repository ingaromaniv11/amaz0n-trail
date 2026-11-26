package com.Talzere8.Qoxhex.presentation.menu

data class MenuUiState(
    val bestScore: Int = 0,
    val selectedStage: Int = 1,
    val stagesUnlocked: Int = 1,
    val totalDelivered: Int = 0,
    val stampsCollected: Int = 0,
    val stampsTotal: Int = 12
)
