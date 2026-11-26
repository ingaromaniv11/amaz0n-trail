package com.Talzere8.Qoxhex.presentation.game

import com.Talzere8.Qoxhex.domain.model.Bay
import com.Talzere8.Qoxhex.domain.model.Parcel

enum class GamePhase {
    LOADING,
    IDLE,
    MOVING,
    DELIVERED,
    MISROUTED,
    FINISHED
}

data class GameUiState(
    val phase: GamePhase = GamePhase.LOADING,
    val stageIndex: Int = 1,
    val seed: Long = 0L,
    val bays: List<Bay> = emptyList(),
    val blocked: Set<Int> = emptySet(),
    val parcel: Parcel? = null,
    val parcelNumber: Int = 0,
    val quota: Int = 6,
    val delivered: Int = 0,
    val cartRow: Int = 0,
    val cartIndex: Int = 0,
    val secondsLeft: Int = 9,
    val score: Int = 0,
    val strikes: Int = 0,
    val combo: Float = 1f,
    val expressCharged: Boolean = false,
    val expressActive: Boolean = false,
    val leftOpen: Boolean = true,
    val rightOpen: Boolean = true,
    val tapedNotice: Boolean = false,
    val shakeKey: Int = 0,
    val flashBay: Int = -1,
    val colourCodes: Boolean = false,
    val routeHints: Boolean = true,
    val paused: Boolean = false
)
