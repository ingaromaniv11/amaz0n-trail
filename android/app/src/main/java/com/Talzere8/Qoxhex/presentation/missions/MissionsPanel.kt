package com.Talzere8.Qoxhex.presentation.missions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.Talzere8.Qoxhex.core.theme.SuccessGreen
import com.Talzere8.Qoxhex.core.theme.SurfaceDeep
import com.Talzere8.Qoxhex.core.theme.SurfaceSoft
import com.Talzere8.Qoxhex.core.theme.TextSecondary
import com.Talzere8.Qoxhex.core.ui.EmptyStateMessage
import com.Talzere8.Qoxhex.core.ui.PrimaryButton
import com.Talzere8.Qoxhex.core.ui.ProgressRail
import com.Talzere8.Qoxhex.core.ui.ScreenHeader
import com.Talzere8.Qoxhex.core.ui.SecondaryButton
import com.Talzere8.Qoxhex.core.ui.TagChip
import com.Talzere8.Qoxhex.core.ui.TrailBackground
import com.Talzere8.Qoxhex.core.ui.TrailResources
import com.Talzere8.Qoxhex.domain.model.MissionSlot

@Composable
fun MissionsPanel(onMenu: () -> Unit) {
    val missionsViewModel: MissionsViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by missionsViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        missionsViewModel.refresh()
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
                title = stringResource(R.string.missions_title),
                subtitle = stringResource(R.string.missions_subtitle)
            )
            Spacer(modifier = Modifier.height(18.dp))
            if (state.slots.isEmpty()) {
                EmptyStateMessage(text = stringResource(R.string.missions_empty))
            }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                for (slot in state.slots) {
                    MissionCard(slot = slot, onClaim = { missionsViewModel.claim(slot.position) })
                }
            }
            if (state.claimedStamp > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.ic_stamp),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(SuccessGreen),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(
                            R.string.stamp_earned,
                            stringResource(TrailResources.stampLabel(state.claimedStamp))
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = SuccessGreen
                    )
                }
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
private fun MissionCard(slot: MissionSlot, onClaim: () -> Unit) {
    val readyText = stringResource(R.string.claim)
    val waitingText = stringResource(R.string.state_locked)
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = BorderStroke(1.dp, if (slot.complete) SuccessGreen else SurfaceDeep),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(108.dp)
            .semantics {
                stateDescription = if (slot.complete) readyText else waitingText
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(TrailResources.missionLabel(slot.missionId)),
                    style = MaterialTheme.typography.titleMedium,
                    color = Graphite
                )
                Spacer(modifier = Modifier.height(10.dp))
                ProgressRail(
                    filled = slot.progress,
                    total = slot.target,
                    fill = Cobalt,
                    track = SurfaceDeep
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.mission_progress, slot.progress, slot.target),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(contentAlignment = Alignment.Center) {
                if (slot.complete) {
                    SecondaryButton(
                        text = readyText,
                        onClick = onClaim,
                        accent = SuccessGreen
                    )
                } else {
                    TagChip(
                        text = stringResource(R.string.mission_progress, slot.progress, slot.target),
                        accent = Cobalt
                    )
                }
            }
        }
    }
}
