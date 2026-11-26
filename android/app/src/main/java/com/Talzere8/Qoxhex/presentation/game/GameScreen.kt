package com.Talzere8.Qoxhex.presentation.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Talzere8.Qoxhex.R
import com.Talzere8.Qoxhex.core.config.GameConfig
import com.Talzere8.Qoxhex.core.di.AppViewModelFactory
import com.Talzere8.Qoxhex.core.theme.BackgroundRaised
import com.Talzere8.Qoxhex.core.theme.CardWhite
import com.Talzere8.Qoxhex.core.theme.Cobalt
import com.Talzere8.Qoxhex.core.theme.Graphite
import com.Talzere8.Qoxhex.core.theme.Orange
import com.Talzere8.Qoxhex.core.theme.SurfaceDeep
import com.Talzere8.Qoxhex.core.theme.SurfaceSoft
import com.Talzere8.Qoxhex.core.theme.TextSecondary
import com.Talzere8.Qoxhex.core.theme.TextTertiary
import com.Talzere8.Qoxhex.core.ui.ParcelSlip
import com.Talzere8.Qoxhex.core.ui.PrimaryButton
import com.Talzere8.Qoxhex.core.ui.ShelfLattice
import com.Talzere8.Qoxhex.core.ui.StatCard
import com.Talzere8.Qoxhex.core.ui.StrikePips
import com.Talzere8.Qoxhex.core.ui.TagChip
import com.Talzere8.Qoxhex.core.ui.TrailBackground
import com.Talzere8.Qoxhex.core.ui.TrailResources
import com.Talzere8.Qoxhex.core.ui.comboLabel
import com.Talzere8.Qoxhex.core.ui.groupedNumber
import com.Talzere8.Qoxhex.domain.model.ShiftResult
import com.Talzere8.Qoxhex.presentation.dialog.PauseDialog

@Composable
fun GameScreen(
    runId: Long,
    onMenu: () -> Unit,
    onShiftEnded: (ShiftResult) -> Unit
) {
    val gameViewModel: GameViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by gameViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(runId) {
        gameViewModel.beginShift(runId)
    }

    LaunchedEffect(Unit) {
        gameViewModel.events.collect { result ->
            onShiftEnded(result)
        }
    }

    DisposableEffect(Unit) {
        onDispose { gameViewModel.pause() }
    }

    GameContent(
        state = state,
        onLeft = { gameViewModel.move(false) },
        onRight = { gameViewModel.move(true) },
        onBranch = { row, index, right -> gameViewModel.onBranch(row, index, right) },
        onPause = { gameViewModel.togglePause(true) },
        onResume = { gameViewModel.togglePause(false) },
        onMenu = onMenu
    )
}

@Composable
private fun GameContent(
    state: GameUiState,
    onLeft: () -> Unit,
    onRight: () -> Unit,
    onBranch: (Int, Int, Boolean) -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onMenu: () -> Unit
) {
    if (state.paused) {
        PauseDialog(onResume = onResume, onMenu = onMenu)
    }
    TrailBackground(
        overlay = listOf(
            BackgroundRaised.copy(alpha = 0.90f),
            SurfaceSoft.copy(alpha = 0.92f),
            SurfaceDeep.copy(alpha = 0.95f)
        ),
        imageAlpha = 0.20f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp)
        ) {
            GameHeader(state = state, onPause = onPause)
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp)
            ) {
                val parcel = state.parcel
                if (parcel != null) {
                    ParcelSlip(parcel = parcel, showCode = state.colourCodes)
                }
                Spacer(modifier = Modifier.weight(1f))
                Column(horizontalAlignment = Alignment.End) {
                    if (state.tapedNotice) {
                        TagChip(text = stringResource(R.string.shelf_taped), accent = Orange)
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    if (state.expressActive || state.expressCharged) {
                        TagChip(text = stringResource(R.string.stat_express), accent = Cobalt)
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                ShelfLattice(
                    bays = state.bays,
                    blocked = state.blocked,
                    cartRow = state.cartRow,
                    cartIndex = state.cartIndex,
                    parcelColour = state.parcel?.colour,
                    showCodes = state.colourCodes,
                    routeHints = state.routeHints,
                    flashBay = state.flashBay,
                    shakeKey = state.shakeKey,
                    onBranch = onBranch,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.controls_hint),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StatCard(
                    label = stringResource(R.string.stat_parcel),
                    value = stringResource(
                        R.string.parcel_counter,
                        state.parcelNumber,
                        state.quota
                    ),
                    accent = Cobalt,
                    modifier = Modifier.weight(1f)
                )
                if (state.expressActive) {
                    StatCard(
                        label = stringResource(R.string.stat_express),
                        value = stringResource(R.string.express_value),
                        accent = Orange,
                        modifier = Modifier.weight(1f),
                        container = Orange.copy(alpha = 0.12f)
                    )
                } else {
                    StatCard(
                        label = stringResource(R.string.stat_time),
                        value = stringResource(R.string.time_value, state.secondsLeft),
                        accent = Graphite,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (state.combo > 1f) {
                    StatCard(
                        label = stringResource(R.string.stat_combo),
                        value = stringResource(R.string.combo_value, comboLabel(state.combo)),
                        accent = Orange,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
            ) {
                val openText = stringResource(R.string.state_open)
                val closedText = stringResource(R.string.state_locked)
                PrimaryButton(
                    text = stringResource(R.string.go_left),
                    onClick = onLeft,
                    height = 72.dp,
                    accentStart = if (state.leftOpen) Cobalt else SurfaceDeep,
                    accentEnd = if (state.leftOpen) Cobalt else SurfaceDeep,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .semantics {
                            stateDescription = if (state.leftOpen) openText else closedText
                        }
                )
                PrimaryButton(
                    text = stringResource(R.string.go_right),
                    onClick = onRight,
                    height = 72.dp,
                    accentStart = if (state.rightOpen) Cobalt else SurfaceDeep,
                    accentEnd = if (state.rightOpen) Cobalt else SurfaceDeep,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .semantics {
                            stateDescription = if (state.rightOpen) openText else closedText
                        }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun GameHeader(state: GameUiState, onPause: () -> Unit) {
    val scoreText = groupedNumber(state.score)
    val scoreDescription = stringResource(R.string.score_desc, scoreText)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(TrailResources.stageLabel(state.stageIndex)),
                style = MaterialTheme.typography.titleMedium,
                color = Graphite
            )
            Spacer(modifier = Modifier.height(4.dp))
            StrikePips(count = state.strikes, max = GameConfig.STRIKES_ALLOWED)
        }
        Text(
            text = scoreText,
            style = MaterialTheme.typography.titleLarge,
            color = Cobalt,
            modifier = Modifier.semantics {
                contentDescription = scoreDescription
                liveRegion = LiveRegionMode.Polite
            }
        )
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(color = CardWhite)
                .clickable { onPause() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(TrailResources.pauseGlyph()),
                contentDescription = stringResource(R.string.pause_desc),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}
