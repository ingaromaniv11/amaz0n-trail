package com.Talzere8.Qoxhex.presentation.collection

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
import com.Talzere8.Qoxhex.core.theme.BackgroundRaised
import com.Talzere8.Qoxhex.core.theme.CardWhite
import com.Talzere8.Qoxhex.core.theme.Graphite
import com.Talzere8.Qoxhex.core.theme.SurfaceDeep
import com.Talzere8.Qoxhex.core.theme.SurfaceSoft
import com.Talzere8.Qoxhex.core.theme.TextSecondary
import com.Talzere8.Qoxhex.core.theme.TextTertiary
import com.Talzere8.Qoxhex.core.ui.EmptyStateMessage
import com.Talzere8.Qoxhex.core.ui.PrimaryButton
import com.Talzere8.Qoxhex.core.ui.ScreenHeader
import com.Talzere8.Qoxhex.core.ui.TrailBackground
import com.Talzere8.Qoxhex.core.ui.TrailResources

@Composable
fun CollectionPanel(onMenu: () -> Unit) {
    val collectionViewModel: CollectionViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by collectionViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        collectionViewModel.refresh()
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    ScreenHeader(
                        title = stringResource(R.string.collection_title),
                        subtitle = stringResource(
                            R.string.collection_counter,
                            state.unlockedCount,
                            state.total
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
            if (state.unlockedCount == 0) {
                EmptyStateMessage(text = stringResource(R.string.collection_empty))
            }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                for (rowIndex in 0 until 4) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(112.dp)
                    ) {
                        for (columnIndex in 0 until 3) {
                            val position = rowIndex * 3 + columnIndex
                            val tile = state.tiles.getOrNull(position)
                            if (tile != null) {
                                StampCard(
                                    tile = tile,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.collection_disclaimer),
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(14.dp))
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
private fun StampCard(tile: StampTile, modifier: Modifier = Modifier) {
    val name = stringResource(TrailResources.stampLabel(tile.id))
    val condition = stringResource(TrailResources.stampCondition(tile.id))
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (tile.unlocked) CardWhite else SurfaceSoft
        ),
        border = BorderStroke(1.dp, SurfaceDeep),
        elevation = CardDefaults.cardElevation(defaultElevation = if (tile.unlocked) 3.dp else 0.dp),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp, vertical = 8.dp)
        ) {
            if (tile.unlocked) {
                Image(
                    painter = painterResource(TrailResources.sprite(tile.spriteSlot)),
                    contentDescription = name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(42.dp)
                )
                Text(
                    text = name,
                    style = MaterialTheme.typography.labelSmall,
                    color = Graphite,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 6.dp)
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.ic_lock),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(TextTertiary),
                    modifier = Modifier.size(26.dp)
                )
                Text(
                    text = condition,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}
