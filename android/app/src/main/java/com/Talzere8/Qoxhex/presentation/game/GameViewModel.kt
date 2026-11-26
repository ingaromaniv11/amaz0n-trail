package com.Talzere8.Qoxhex.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Talzere8.Qoxhex.core.config.GameConfig
import com.Talzere8.Qoxhex.data.sample.SampleData
import com.Talzere8.Qoxhex.domain.model.Lattice
import com.Talzere8.Qoxhex.domain.model.Parcel
import com.Talzere8.Qoxhex.domain.model.ShiftLayout
import com.Talzere8.Qoxhex.domain.model.ShiftResult
import com.Talzere8.Qoxhex.domain.model.ShiftTally
import com.Talzere8.Qoxhex.domain.model.ShiftVerdict
import com.Talzere8.Qoxhex.domain.repository.CollectionRepository
import com.Talzere8.Qoxhex.domain.repository.MissionRepository
import com.Talzere8.Qoxhex.domain.repository.ProgressRepository
import com.Talzere8.Qoxhex.domain.repository.SettingsRepository
import com.Talzere8.Qoxhex.domain.usecase.EvaluateShiftUseCase
import com.Talzere8.Qoxhex.domain.usecase.GenerateShiftUseCase
import com.Talzere8.Qoxhex.domain.usecase.RouteSolverUseCase
import com.Talzere8.Qoxhex.domain.usecase.ScoreDeliveryUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GameViewModel(
    private val progressRepository: ProgressRepository,
    private val collectionRepository: CollectionRepository,
    private val missionRepository: MissionRepository,
    private val settingsRepository: SettingsRepository,
    private val generateShift: GenerateShiftUseCase,
    private val routeSolver: RouteSolverUseCase,
    private val scoreDelivery: ScoreDeliveryUseCase,
    private val evaluateShift: EvaluateShiftUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val _events = Channel<ShiftResult>(Channel.BUFFERED)
    val events: Flow<ShiftResult> = _events.receiveAsFlow()

    private var activeRunId = 0L
    private var loopJob: Job? = null
    private var running = false

    private var layout: ShiftLayout? = null
    private var parcelTimeoutMs = GameConfig.PARCEL_TIMEOUT_MS
    private var remainingMs = GameConfig.PARCEL_TIMEOUT_MS
    private var elapsedMs = 0L
    private var holdUntilMs = 0L
    private var noticeUntilMs = 0L
    private var cleanStreak = 0
    private var bestCombo = 1f
    private var tidySeals = 0
    private var expressUsed = 0
    private var rushDelivered = 0
    private var attempt = 0

    fun beginShift(runId: Long) {
        if (runId == activeRunId) return
        activeRunId = runId
        attempt += 1
        val stage = SampleData.stage(progressRepository.selectedStage())
        val built = generateShift(stage, attempt)
        layout = built
        parcelTimeoutMs = baseTimeout()
        remainingMs = parcelTimeoutMs
        elapsedMs = 0L
        holdUntilMs = 0L
        noticeUntilMs = 0L
        cleanStreak = 0
        bestCombo = 1f
        tidySeals = 0
        expressUsed = 0
        rushDelivered = 0
        val firstParcel = built.parcels.firstOrNull()
        _state.value = GameUiState(
            phase = GamePhase.IDLE,
            stageIndex = stage.index,
            seed = built.seed,
            bays = built.bays,
            blocked = built.blocked,
            parcel = firstParcel,
            parcelNumber = 1,
            quota = stage.quota,
            delivered = 0,
            cartRow = 0,
            cartIndex = 0,
            secondsLeft = secondsOf(remainingMs),
            colourCodes = settingsRepository.colourCodes(),
            routeHints = settingsRepository.routeHints()
        )
        applyOpenSides()
        startLoop()
    }

    fun pause() {
        running = false
        loopJob?.cancel()
        loopJob = null
    }

    fun togglePause(value: Boolean) {
        _state.update { it.copy(paused = value) }
        if (value) {
            running = false
            loopJob?.cancel()
            loopJob = null
        } else if (_state.value.phase != GamePhase.FINISHED) {
            startLoop()
        }
    }

    fun move(right: Boolean) {
        val current = _state.value
        if (current.phase != GamePhase.IDLE || current.paused) return
        if (current.cartRow >= GameConfig.ROWS) return
        val open = if (right) current.rightOpen else current.leftOpen
        if (!open) {
            noticeUntilMs = elapsedMs + GameConfig.TAPED_NOTICE_MS
            _state.update {
                it.copy(tapedNotice = true, shakeKey = it.shakeKey + 1)
            }
            return
        }
        val nextRow = current.cartRow + 1
        val nextIndex = if (right) current.cartIndex + 1 else current.cartIndex
        holdUntilMs = elapsedMs + GameConfig.MOVE_LOCK_MS
        _state.update {
            it.copy(
                phase = GamePhase.MOVING,
                cartRow = nextRow,
                cartIndex = nextIndex,
                tapedNotice = false
            )
        }
    }

    fun onBranch(row: Int, index: Int, right: Boolean) {
        val current = _state.value
        if (row != current.cartRow || index != current.cartIndex) return
        move(right)
    }

    private fun baseTimeout(): Long =
        if (settingsRepository.relaxedTimer()) GameConfig.RELAXED_TIMEOUT_MS
        else GameConfig.PARCEL_TIMEOUT_MS

    private fun timeoutFor(parcel: Parcel?): Long {
        val base = baseTimeout()
        if (parcel != null && parcel.rush && base == GameConfig.PARCEL_TIMEOUT_MS) {
            return GameConfig.RUSH_TIMEOUT_MS
        }
        return base
    }

    private fun secondsOf(value: Long): Int = ((value + 999L) / 1000L).toInt().coerceAtLeast(0)

    private fun applyOpenSides() {
        val current = _state.value
        if (current.cartRow >= GameConfig.ROWS) {
            _state.update { it.copy(leftOpen = false, rightOpen = false) }
            return
        }
        val sides = routeSolver.openSides(current.blocked, current.cartRow, current.cartIndex)
        _state.update { it.copy(leftOpen = sides.first, rightOpen = sides.second) }
    }

    private fun startLoop() {
        loopJob?.cancel()
        running = true
        loopJob = viewModelScope.launch {
            while (isActive && running) {
                delay(GameConfig.TICK_MS)
                tick()
            }
        }
    }

    private fun tick() {
        val current = _state.value
        if (current.phase == GamePhase.FINISHED) return
        elapsedMs += GameConfig.TICK_MS

        if (current.tapedNotice && elapsedMs >= noticeUntilMs) {
            _state.update { it.copy(tapedNotice = false) }
        }

        when (current.phase) {
            GamePhase.MOVING -> {
                if (elapsedMs >= holdUntilMs) resolveMove()
                return
            }
            GamePhase.DELIVERED, GamePhase.MISROUTED -> {
                if (elapsedMs >= holdUntilMs) advanceParcel()
                return
            }
            else -> Unit
        }

        if (elapsedMs >= GameConfig.ROUND_HARD_CAP_MS) {
            finish(ShiftVerdict.BELL)
            return
        }

        remainingMs -= GameConfig.TICK_MS
        if (remainingMs <= 0L) {
            registerMisroute()
            return
        }
        val seconds = secondsOf(remainingMs)
        if (seconds != current.secondsLeft) {
            _state.update { it.copy(secondsLeft = seconds) }
        }
    }

    private fun resolveMove() {
        val current = _state.value
        if (current.cartRow < GameConfig.ROWS) {
            _state.update { it.copy(phase = GamePhase.IDLE) }
            applyOpenSides()
            return
        }
        val parcel = current.parcel
        val bay = current.bays.firstOrNull { it.index == current.cartIndex }
        val correct = parcel != null && bay != null &&
            bay.colour == parcel.colour && bay.town == parcel.town
        if (correct) registerDelivery() else registerMisroute()
    }

    private fun registerDelivery() {
        val current = _state.value
        val parcel = current.parcel
        val seconds = secondsOf(remainingMs)
        val express = current.expressActive
        val rush = parcel != null && parcel.rush
        val gained = scoreDelivery(seconds, current.combo, express, rush)
        val nextCombo = scoreDelivery.nextCombo(current.combo)
        if (nextCombo > bestCombo) bestCombo = nextCombo
        if (seconds * 2 >= secondsOf(parcelTimeoutMs)) tidySeals += 1
        if (express) expressUsed += 1
        if (rush) rushDelivered += 1
        cleanStreak += 1
        val charged = cleanStreak >= GameConfig.EXPRESS_STREAK
        holdUntilMs = elapsedMs + GameConfig.DELIVERED_HOLD_MS
        _state.update {
            it.copy(
                phase = GamePhase.DELIVERED,
                score = it.score + gained,
                delivered = it.delivered + 1,
                combo = nextCombo,
                expressCharged = charged,
                expressActive = false,
                flashBay = it.cartIndex
            )
        }
    }

    private fun registerMisroute() {
        cleanStreak = 0
        holdUntilMs = elapsedMs + GameConfig.MISROUTE_HOLD_MS
        _state.update {
            it.copy(
                phase = GamePhase.MISROUTED,
                strikes = it.strikes + 1,
                combo = 1f,
                expressCharged = false,
                expressActive = false,
                shakeKey = it.shakeKey + 1,
                flashBay = -1
            )
        }
    }

    private fun advanceParcel() {
        val current = _state.value
        if (current.strikes >= GameConfig.STRIKES_ALLOWED) {
            finish(ShiftVerdict.JAMMED)
            return
        }
        if (current.delivered >= current.quota) {
            finish(ShiftVerdict.COMPLETE)
            return
        }
        val parcels = layout?.parcels ?: emptyList()
        val nextNumber = current.parcelNumber + 1
        val nextParcel = parcels.getOrNull(nextNumber - 1) ?: parcels.lastOrNull()
        parcelTimeoutMs = timeoutFor(nextParcel)
        remainingMs = parcelTimeoutMs
        val useExpress = current.expressCharged
        val startRowValue = if (useExpress) 1 else 0
        val startIndexValue = if (useExpress) expressEntry(current.blocked) else 0
        _state.update {
            it.copy(
                phase = GamePhase.IDLE,
                parcel = nextParcel,
                parcelNumber = nextNumber,
                cartRow = startRowValue,
                cartIndex = startIndexValue,
                secondsLeft = secondsOf(remainingMs),
                expressCharged = false,
                expressActive = useExpress,
                flashBay = -1
            )
        }
        applyOpenSides()
    }

    private fun expressEntry(blocked: Set<Int>): Int {
        val leftOpen = !blocked.contains(Lattice.branchKey(0, 0, false))
        return if (leftOpen) 0 else 1
    }

    private fun finish(verdict: ShiftVerdict) {
        running = false
        val current = _state.value
        _state.update { it.copy(phase = GamePhase.FINISHED, tapedNotice = false) }
        val tally = ShiftTally(
            stageIndex = current.stageIndex,
            verdict = verdict,
            score = current.score,
            delivered = current.delivered,
            quota = current.quota,
            strikes = current.strikes,
            bestCombo = bestCombo,
            tidySeals = tidySeals,
            expressUsed = expressUsed,
            rushDelivered = rushDelivered
        )
        val result = evaluateShift(
            tally = tally,
            bestScore = progressRepository.bestScore(),
            stagesUnlocked = progressRepository.stagesUnlocked(),
            unlockedStamps = collectionRepository.unlocked()
        )
        progressRepository.recordShift(
            stageIndex = result.stageIndex,
            score = result.score,
            delivered = result.delivered,
            perfect = result.perfect
        )
        if (result.stageUnlocked > 0) progressRepository.unlockStage(result.stageUnlocked)
        if (result.stampEarned > 0) collectionRepository.unlock(result.stampEarned)
        missionRepository.advance(
            delivered = result.delivered,
            bestCombo = result.bestCombo,
            perfect = result.perfect,
            expressUsed = result.expressUsed,
            noStrikeClear = result.perfect,
            rushDelivered = rushDelivered
        )
        viewModelScope.launch {
            delay(GameConfig.FINISH_HOLD_MS)
            _events.send(result)
        }
    }
}
