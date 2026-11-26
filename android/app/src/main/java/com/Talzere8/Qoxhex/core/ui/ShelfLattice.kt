package com.Talzere8.Qoxhex.core.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.Talzere8.Qoxhex.R
import com.Talzere8.Qoxhex.core.config.GameConfig
import com.Talzere8.Qoxhex.core.theme.CardWhite
import com.Talzere8.Qoxhex.core.theme.Cobalt
import com.Talzere8.Qoxhex.core.theme.Graphite
import com.Talzere8.Qoxhex.core.theme.Orange
import com.Talzere8.Qoxhex.core.theme.SuccessGreen
import com.Talzere8.Qoxhex.core.theme.SurfaceDeep
import com.Talzere8.Qoxhex.core.theme.SurfaceSoft
import com.Talzere8.Qoxhex.core.theme.TextSecondary
import com.Talzere8.Qoxhex.core.theme.TextTertiary
import com.Talzere8.Qoxhex.domain.model.Bay
import com.Talzere8.Qoxhex.domain.model.Lattice
import com.Talzere8.Qoxhex.domain.model.RouteColour
import kotlin.math.sin

private const val BOARD_PAD = 10
private const val BOARD_BORDER = 2

@Composable
fun ShelfLattice(
    bays: List<Bay>,
    blocked: Set<Int>,
    cartRow: Int,
    cartIndex: Int,
    parcelColour: RouteColour?,
    showCodes: Boolean,
    routeHints: Boolean,
    flashBay: Int,
    shakeKey: Int,
    onBranch: (Int, Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val cartDescription = stringResource(R.string.cart_desc)
    val shake = remember { Animatable(0f) }
    LaunchedEffect(shakeKey) {
        if (shakeKey > 0) {
            shake.snapTo(1f)
            shake.animateTo(0f, tween(durationMillis = 320))
        }
    }
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.TopCenter) {
        val frame: Dp = (BOARD_PAD + BOARD_BORDER).dp
        val boardMax = min(maxWidth, 380.dp)
        val cellValue = ((boardMax - frame * 2).value / GameConfig.COLS.toFloat())
        val cell: Dp = kotlin.math.floor(cellValue.toDouble()).toFloat().dp
        val boardWidth = cell * GameConfig.COLS + frame * 2
        val rowGap = cell * 0.92f
        val bayTop = frame + rowGap * 4.2f
        val bayHeight = 76.dp
        val boardHeight = bayTop + bayHeight + frame
        val nodeSize = min(cell * 0.62f, 52.dp)
        val cartSize = cell * 0.74f

        fun nodeX(row: Int, index: Int): Dp = frame + cell * (2.5f + index - row * 0.5f)
        fun nodeY(row: Int): Dp = if (row >= GameConfig.ROWS) bayTop else frame + rowGap * (row + 0.5f)

        val cartX by animateDpAsState(
            targetValue = nodeX(cartRow, cartIndex) - cartSize / 2,
            animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing)
        )
        val cartY by animateDpAsState(
            targetValue = nodeY(cartRow) - cartSize / 2,
            animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing)
        )

        Box(
            modifier = Modifier
                .width(boardWidth)
                .height(boardHeight)
                .graphicsLayer { translationX = sin(shake.value * 18.8f) * 14f }
                .clip(RoundedCornerShape(22.dp))
                .background(color = CardWhite.copy(alpha = 0.86f))
                .border(BOARD_BORDER.dp, SurfaceDeep, RoundedCornerShape(22.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                for (row in 0 until GameConfig.ROWS) {
                    for (index in 0..row) {
                        val start = Offset(nodeX(row, index).toPx(), nodeY(row).toPx())
                        for (right in listOf(false, true)) {
                            val targetIndex = if (right) index + 1 else index
                            val end = Offset(
                                nodeX(row + 1, targetIndex).toPx(),
                                nodeY(row + 1).toPx()
                            )
                            val taped = blocked.contains(Lattice.branchKey(row, index, right))
                            val legal = row == cartRow && index == cartIndex
                            drawLine(
                                color = if (taped) TextTertiary else Cobalt.copy(alpha = 0.55f),
                                start = start,
                                end = end,
                                strokeWidth = 5.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                            if (taped) {
                                for (step in 1..4) {
                                    val t = step / 5f
                                    val px = start.x + (end.x - start.x) * t
                                    val py = start.y + (end.y - start.y) * t
                                    drawLine(
                                        color = Orange.copy(alpha = 0.9f),
                                        start = Offset(px - 7.dp.toPx(), py - 7.dp.toPx()),
                                        end = Offset(px + 7.dp.toPx(), py + 7.dp.toPx()),
                                        strokeWidth = 3.dp.toPx(),
                                        cap = StrokeCap.Round
                                    )
                                }
                            } else if (legal && routeHints) {
                                drawLine(
                                    color = Orange,
                                    start = start,
                                    end = end,
                                    strokeWidth = 2.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                            }
                        }
                    }
                }
                for (row in 0 until GameConfig.ROWS) {
                    for (index in 0..row) {
                        drawCircle(
                            color = SurfaceSoft,
                            radius = nodeSize.toPx() / 2f,
                            center = Offset(nodeX(row, index).toPx(), nodeY(row).toPx())
                        )
                        drawCircle(
                            color = Cobalt.copy(alpha = 0.35f),
                            radius = nodeSize.toPx() / 2f,
                            center = Offset(nodeX(row, index).toPx(), nodeY(row).toPx()),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                        )
                    }
                }
            }

            if (cartRow < GameConfig.ROWS) {
                for (right in listOf(false, true)) {
                    val targetIndex = if (right) cartIndex + 1 else cartIndex
                    val midX = (nodeX(cartRow, cartIndex) + nodeX(cartRow + 1, targetIndex)) / 2f
                    val midY = (nodeY(cartRow) + nodeY(cartRow + 1)) / 2f
                    Box(
                        modifier = Modifier
                            .offset(x = midX - 24.dp, y = midY - 24.dp)
                            .size(48.dp)
                            .clickable { onBranch(cartRow, cartIndex, right) }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .offset(x = cartX, y = cartY)
                    .size(cartSize)
                    .semantics { contentDescription = cartDescription },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(TrailResources.cart()),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
                if (parcelColour != null) {
                    Image(
                        painter = painterResource(TrailResources.sprite(routeSpriteSlot(parcelColour))),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(cartSize * 0.5f)
                            .offset(y = -(cartSize * 0.28f))
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(0.dp),
                modifier = Modifier
                    .offset(x = frame, y = bayTop)
                    .width(cell * GameConfig.COLS)
                    .height(bayHeight)
            ) {
                for (bay in bays) {
                    BayTile(
                        bay = bay,
                        showCodes = showCodes,
                        flashing = flashBay == bay.index,
                        modifier = Modifier
                            .width(cell)
                            .fillMaxHeight()
                            .padding(3.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BayTile(
    bay: Bay,
    showCodes: Boolean,
    flashing: Boolean,
    modifier: Modifier = Modifier
) {
    val townText = stringResource(TrailResources.townLabel(bay.town))
    val colourText = stringResource(TrailResources.colourLabel(bay.colour))
    val description = stringResource(R.string.bay_desc, colourText, townText)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(color = if (flashing) SuccessGreen.copy(alpha = 0.28f) else SurfaceSoft)
            .border(
                width = 1.dp,
                color = if (flashing) SuccessGreen else SurfaceDeep,
                shape = RoundedCornerShape(14.dp)
            )
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            RouteSwatch(colour = bay.colour, showCode = showCodes, size = 24.dp)
            Text(
                text = townText,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, start = 1.dp, end = 1.dp)
            )
        }
    }
}
