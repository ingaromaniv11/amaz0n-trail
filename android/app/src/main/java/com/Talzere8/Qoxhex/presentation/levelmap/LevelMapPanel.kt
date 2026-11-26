package com.Talzere8.Qoxhex.presentation.levelmap

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Talzere8.Qoxhex.R
import com.Talzere8.Qoxhex.core.di.AppViewModelFactory
import com.Talzere8.Qoxhex.core.theme.BackgroundBase
import com.Talzere8.Qoxhex.core.theme.BackgroundRaised
import com.Talzere8.Qoxhex.core.theme.CardWhite
import com.Talzere8.Qoxhex.core.theme.Cobalt
import com.Talzere8.Qoxhex.core.theme.Graphite
import com.Talzere8.Qoxhex.core.theme.Orange
import com.Talzere8.Qoxhex.core.theme.SurfaceDeep
import com.Talzere8.Qoxhex.core.theme.SurfaceSoft
import com.Talzere8.Qoxhex.core.theme.TextSecondary
import com.Talzere8.Qoxhex.core.theme.TextTertiary
import com.Talzere8.Qoxhex.core.ui.PrimaryButton
import com.Talzere8.Qoxhex.core.ui.ScreenHeader
import com.Talzere8.Qoxhex.core.ui.TagChip
import com.Talzere8.Qoxhex.core.ui.TrailBackground
import com.Talzere8.Qoxhex.core.ui.TrailResources
import com.Talzere8.Qoxhex.core.ui.groupedNumber

@Composable
fun LevelMapPanel(onMenu: () -> Unit) {
    val levelMapViewModel: LevelMapViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by levelMapViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        levelMapViewModel.refresh()
    }

    TrailBackground(
        overlay = listOf(
            BackgroundBase.copy(alpha = 0.93f),
            BackgroundRaised.copy(alpha = 0.95f),
            SurfaceSoft.copy(alpha = 0.96f)
        ),
        imageAlpha = 0.14f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            ScreenHeader(
                title = stringResource(R.string.levels_title),
                subtitle = stringResource(R.string.levels_subtitle)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                for (node in state.nodes) {
                    StageRow(node = node, onSelect = { levelMapViewModel.select(node.index) })
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            if (state.lockedNotice > 0) {
                Text(
                    text = stringResource(R.string.stage_locked, state.lockedNotice),
                    style = MaterialTheme.typography.labelSmall,
                    color = Orange
                )
            } else {
                Text(
                    text = stringResource(R.string.stage_selected, state.selectedStage),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            PrimaryButton(
                text = stringResource(R.string.menu),
                onClick = onMenu,
                height = 60.dp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun StageRow(node: StageNode, onSelect: () -> Unit) {
    val openText = stringResource(R.string.state_open)
    val lockedText = stringResource(R.string.state_locked)
    val selectedText = stringResource(R.string.state_selected)
    Card(
        onClick = onSelect,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (node.selected) Cobalt.copy(alpha = 0.10f) else CardWhite
        ),
        border = BorderStroke(if (node.selected) 2.dp else 1.dp, if (node.selected) Cobalt else SurfaceDeep),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .semantics {
                stateDescription = when {
                    !node.unlocked -> lockedText
                    node.selected -> selectedText
                    else -> openText
                }
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Box(
                modifier = Modifier.size(28.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(
                        if (node.unlocked) R.drawable.ic_route else R.drawable.ic_lock
                    ),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(if (node.unlocked) Cobalt else TextTertiary),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(
                        R.string.stage_line,
                        node.index,
                        stringResource(TrailResources.stageLabel(node.index))
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (node.unlocked) Graphite else TextTertiary
                )
                if (node.best > 0) {
                    Text(
                        text = stringResource(R.string.stage_best, groupedNumber(node.best)),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
            if (node.selected) {
                TagChip(text = selectedText, accent = Cobalt)
            }
        }
    }
}
