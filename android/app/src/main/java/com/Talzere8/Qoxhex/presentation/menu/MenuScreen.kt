package com.Talzere8.Qoxhex.presentation.menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Talzere8.Qoxhex.R
import com.Talzere8.Qoxhex.core.config.GameConfig
import com.Talzere8.Qoxhex.core.di.AppViewModelFactory
import com.Talzere8.Qoxhex.core.theme.BackgroundBase
import com.Talzere8.Qoxhex.core.theme.BackgroundRaised
import com.Talzere8.Qoxhex.core.theme.CardWhite
import com.Talzere8.Qoxhex.core.theme.Cobalt
import com.Talzere8.Qoxhex.core.theme.Graphite
import com.Talzere8.Qoxhex.core.theme.Orange
import com.Talzere8.Qoxhex.core.theme.SurfaceDeep
import com.Talzere8.Qoxhex.core.theme.TextSecondary
import com.Talzere8.Qoxhex.core.theme.TextTertiary
import com.Talzere8.Qoxhex.core.ui.BentoTile
import com.Talzere8.Qoxhex.core.ui.PrimaryButton
import com.Talzere8.Qoxhex.core.ui.ProgressRail
import com.Talzere8.Qoxhex.core.ui.SecondaryButton
import com.Talzere8.Qoxhex.core.ui.StatCard
import com.Talzere8.Qoxhex.core.ui.TrailBackground
import com.Talzere8.Qoxhex.core.ui.TrailResources
import com.Talzere8.Qoxhex.core.ui.groupedNumber

@Composable
fun MenuScreen(
    onPlay: () -> Unit,
    onLevels: () -> Unit,
    onMissions: () -> Unit,
    onCollection: () -> Unit,
    onTutorial: () -> Unit,
    onSettings: () -> Unit
) {
    val menuViewModel: MenuViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by menuViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        menuViewModel.refresh()
    }

    MenuContent(
        state = state,
        onPlay = onPlay,
        onLevels = onLevels,
        onMissions = onMissions,
        onCollection = onCollection,
        onTutorial = onTutorial,
        onSettings = onSettings
    )
}

@Composable
private fun MenuContent(
    state: MenuUiState,
    onPlay: () -> Unit,
    onLevels: () -> Unit,
    onMissions: () -> Unit,
    onCollection: () -> Unit,
    onTutorial: () -> Unit,
    onSettings: () -> Unit
) {
    var entered by remember { mutableStateOf(false) }
    LaunchedEffect(state.selectedStage) {
        entered = true
    }

    TrailBackground(
        overlay = listOf(
            BackgroundBase.copy(alpha = 0.92f),
            BackgroundRaised.copy(alpha = 0.94f),
            SurfaceDeep.copy(alpha = 0.96f)
        ),
        imageAlpha = 0.16f
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(320.dp)
                    .clip(RoundedCornerShape(160.dp))
                    .background(color = Cobalt.copy(alpha = 0.07f))
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp)
        ) {
            MenuHeader(bestScore = state.bestScore)
            Spacer(modifier = Modifier.height(14.dp))
            AnimatedVisibility(
                visible = entered,
                enter = fadeIn(tween(260)) + slideInVertically(tween(260)) { full -> full / 8 }
            ) {
                HeroTile(state = state)
            }
            Spacer(modifier = Modifier.height(12.dp))
            if (state.totalDelivered > 0 && state.stampsCollected > 0) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatCard(
                        label = stringResource(R.string.stat_parcels),
                        value = groupedNumber(state.totalDelivered),
                        accent = Cobalt,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = stringResource(R.string.stat_stamps),
                        value = state.stampsCollected.toString(),
                        accent = Orange,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(92.dp)
            ) {
                BentoTile(
                    title = stringResource(R.string.levels),
                    caption = stringResource(R.string.levels_caption),
                    glyph = painterResource(R.drawable.ic_route),
                    onClick = onLevels,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                BentoTile(
                    title = stringResource(R.string.missions),
                    caption = stringResource(R.string.missions_caption),
                    glyph = painterResource(R.drawable.ic_mission),
                    onClick = onMissions,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(92.dp)
            ) {
                BentoTile(
                    title = stringResource(R.string.collection),
                    caption = stringResource(R.string.collection_caption),
                    glyph = painterResource(R.drawable.ic_stamp),
                    onClick = onCollection,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                BentoTile(
                    title = stringResource(R.string.tutorial),
                    caption = stringResource(R.string.tutorial_caption),
                    glyph = painterResource(R.drawable.ic_tutorial),
                    onClick = onTutorial,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.menu_hint),
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
                    text = stringResource(R.string.settings),
                    onClick = onSettings,
                    leading = painterResource(R.drawable.ic_settings),
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.stage_line, state.selectedStage, stringResource(TrailResources.stageLabel(state.selectedStage))),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            PrimaryButton(
                text = stringResource(R.string.play),
                onClick = onPlay,
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
private fun MenuHeader(bestScore: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(86.dp)
    ) {
        Image(
            painter = painterResource(TrailResources.logo()),
            contentDescription = stringResource(R.string.logo_desc),
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(44.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.menu_title),
                style = MaterialTheme.typography.displaySmall,
                color = Graphite
            )
            Text(
                text = stringResource(R.string.menu_kicker),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        if (bestScore > 0) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                border = BorderStroke(1.dp, SurfaceDeep),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Image(
                        painter = painterResource(TrailResources.homeGlyph()),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.best_chip, groupedNumber(bestScore)),
                        style = MaterialTheme.typography.labelSmall,
                        color = Graphite
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroTile(state: MenuUiState) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = BorderStroke(1.dp, SurfaceDeep),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(176.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(TrailResources.cart()),
                contentDescription = stringResource(R.string.cart_desc),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(104.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(
                        R.string.stage_line,
                        state.selectedStage,
                        stringResource(TrailResources.stageLabel(state.selectedStage))
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    color = Graphite
                )
                Spacer(modifier = Modifier.height(12.dp))
                ProgressRail(
                    filled = state.stagesUnlocked,
                    total = GameConfig.STAGE_COUNT,
                    fill = Cobalt,
                    track = SurfaceDeep
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(
                        R.string.routes_open,
                        state.stagesUnlocked,
                        GameConfig.STAGE_COUNT
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }
    }
}
