package com.Talzere8.Qoxhex.presentation.result

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Talzere8.Qoxhex.R
import com.Talzere8.Qoxhex.core.di.AppViewModelFactory
import com.Talzere8.Qoxhex.core.theme.BackgroundBase
import com.Talzere8.Qoxhex.core.theme.CardWhite
import com.Talzere8.Qoxhex.core.theme.Cobalt
import com.Talzere8.Qoxhex.core.theme.Graphite
import com.Talzere8.Qoxhex.core.theme.Orange
import com.Talzere8.Qoxhex.core.theme.SuccessGreen
import com.Talzere8.Qoxhex.core.theme.SurfaceDeep
import com.Talzere8.Qoxhex.core.theme.SurfaceSoft
import com.Talzere8.Qoxhex.core.theme.TextSecondary
import com.Talzere8.Qoxhex.core.theme.TextTertiary
import com.Talzere8.Qoxhex.core.ui.PrimaryButton
import com.Talzere8.Qoxhex.core.ui.SecondaryButton
import com.Talzere8.Qoxhex.core.ui.StatCard
import com.Talzere8.Qoxhex.core.ui.TagChip
import com.Talzere8.Qoxhex.core.ui.TrailBackground
import com.Talzere8.Qoxhex.core.ui.TrailResources
import com.Talzere8.Qoxhex.core.ui.comboLabel
import com.Talzere8.Qoxhex.core.ui.groupedNumber
import com.Talzere8.Qoxhex.core.ui.routeSpriteSlot
import com.Talzere8.Qoxhex.data.sample.SampleData
import com.Talzere8.Qoxhex.domain.model.ShiftResult
import com.Talzere8.Qoxhex.domain.model.ShiftVerdict

@Composable
fun ResultScreen(
    result: ShiftResult,
    onPlayAgain: () -> Unit,
    onMenu: () -> Unit,
    onCollection: () -> Unit
) {
    val resultViewModel: ResultViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by resultViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(result) {
        resultViewModel.refresh()
    }

    ResultContent(
        result = result,
        state = state,
        onPlayAgain = onPlayAgain,
        onMenu = onMenu,
        onCollection = onCollection
    )
}

@Composable
private fun ResultContent(
    result: ShiftResult,
    state: ResultUiState,
    onPlayAgain: () -> Unit,
    onMenu: () -> Unit,
    onCollection: () -> Unit
) {
    var entered by remember { mutableStateOf(false) }
    LaunchedEffect(result) {
        entered = true
    }

    TrailBackground(
        overlay = listOf(
            BackgroundBase.copy(alpha = 0.93f),
            SurfaceSoft.copy(alpha = 0.95f),
            SurfaceDeep.copy(alpha = 0.97f)
        ),
        imageAlpha = 0.18f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(28.dp))
            AnimatedVisibility(
                visible = entered,
                enter = fadeIn(tween(380)) + scaleIn(tween(380), initialScale = 0.92f)
            ) {
                VerdictCard(result = result)
            }
            Spacer(modifier = Modifier.height(16.dp))
            ScoreBlock(result = result)
            Spacer(modifier = Modifier.height(16.dp))
            StatGrid(result = result)
            Spacer(modifier = Modifier.height(14.dp))
            RewardStrip(result = result, stampsCollected = state.stampsCollected)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.best_recap, groupedNumber(state.bestScore)),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                SecondaryButton(
                    text = stringResource(R.string.menu),
                    onClick = onMenu,
                    leading = painterResource(TrailResources.homeGlyph()),
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(onClick = onCollection) {
                        Text(
                            text = stringResource(R.string.collection),
                            style = MaterialTheme.typography.labelLarge,
                            color = Cobalt
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            PrimaryButton(
                text = stringResource(R.string.play_again),
                onClick = onPlayAgain,
                height = 68.dp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.collection_disclaimer),
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(38.dp))
        }
    }
}

@Composable
private fun VerdictCard(result: ShiftResult) {
    val headline = when (result.verdict) {
        ShiftVerdict.COMPLETE -> stringResource(R.string.verdict_complete)
        ShiftVerdict.JAMMED -> stringResource(R.string.verdict_jammed)
        ShiftVerdict.BELL -> stringResource(R.string.verdict_bell)
    }
    val reason = when (result.verdict) {
        ShiftVerdict.COMPLETE -> stringResource(R.string.reason_complete, result.delivered)
        ShiftVerdict.JAMMED -> stringResource(R.string.reason_jammed)
        ShiftVerdict.BELL -> stringResource(R.string.reason_bell)
    }
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = BorderStroke(1.dp, SurfaceDeep),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp)
        ) {
            Text(
                text = headline,
                style = MaterialTheme.typography.headlineMedium,
                color = if (result.verdict == ShiftVerdict.COMPLETE) SuccessGreen else Graphite,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = reason,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            if (result.perfect) {
                Spacer(modifier = Modifier.height(10.dp))
                TagChip(text = stringResource(R.string.perfect_shift), accent = Orange)
            }
        }
    }
}

@Composable
private fun ScoreBlock(result: ShiftResult) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = groupedNumber(result.score),
            style = MaterialTheme.typography.displaySmall,
            color = Cobalt
        )
        if (result.newBest) {
            Spacer(modifier = Modifier.width(12.dp))
            TagChip(text = stringResource(R.string.new_best), accent = SuccessGreen)
        }
    }
}

@Composable
private fun StatGrid(result: ShiftResult) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            StatCard(
                label = stringResource(R.string.res_delivered),
                value = stringResource(
                    R.string.res_delivered_value,
                    result.delivered,
                    result.quota
                ),
                accent = Cobalt,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = stringResource(R.string.res_best_combo),
                value = stringResource(R.string.combo_value, comboLabel(result.bestCombo)),
                accent = Orange,
                modifier = Modifier.weight(1f)
            )
        }
        if (result.tidySeals > 0 && result.expressUsed > 0) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StatCard(
                    label = stringResource(R.string.res_tidy),
                    value = result.tidySeals.toString(),
                    accent = SuccessGreen,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = stringResource(R.string.res_express),
                    value = result.expressUsed.toString(),
                    accent = Graphite,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun RewardStrip(result: ShiftResult, stampsCollected: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (result.stampEarned > 0) {
            val definition = SampleData.stamp(result.stampEarned)
            val stampName = stringResource(TrailResources.stampLabel(result.stampEarned))
            Image(
                painter = painterResource(TrailResources.sprite(definition.spriteSlot)),
                contentDescription = stampName,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(44.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = stringResource(R.string.stamp_earned, stampName),
                    style = MaterialTheme.typography.labelSmall,
                    color = Graphite
                )
                if (result.stageUnlocked > 0) {
                    Text(
                        text = stringResource(R.string.stage_open, result.stageUnlocked),
                        style = MaterialTheme.typography.labelSmall,
                        color = Cobalt,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        } else if (result.stageUnlocked > 0) {
            Image(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.stage_open, result.stageUnlocked),
                style = MaterialTheme.typography.labelSmall,
                color = Cobalt
            )
        } else {
            Image(
                painter = painterResource(TrailResources.sprite(routeSpriteSlot(com.Talzere8.Qoxhex.domain.model.RouteColour.PAPER))),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.no_stamp),
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(
                R.string.collection_counter,
                stampsCollected,
                com.Talzere8.Qoxhex.core.config.GameConfig.STAMP_COUNT
            ),
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
    }
}
