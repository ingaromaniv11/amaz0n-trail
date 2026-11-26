package com.Talzere8.Qoxhex.presentation.tutorial

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.Talzere8.Qoxhex.R
import com.Talzere8.Qoxhex.core.theme.BackgroundBase
import com.Talzere8.Qoxhex.core.theme.BackgroundRaised
import com.Talzere8.Qoxhex.core.theme.CardWhite
import com.Talzere8.Qoxhex.core.theme.Cobalt
import com.Talzere8.Qoxhex.core.theme.Graphite
import com.Talzere8.Qoxhex.core.theme.Orange
import com.Talzere8.Qoxhex.core.theme.SurfaceDeep
import com.Talzere8.Qoxhex.core.theme.SurfaceSoft
import com.Talzere8.Qoxhex.core.theme.TextSecondary
import com.Talzere8.Qoxhex.core.ui.PrimaryButton
import com.Talzere8.Qoxhex.core.ui.ProgressRail
import com.Talzere8.Qoxhex.core.ui.ScreenHeader
import com.Talzere8.Qoxhex.core.ui.SecondaryButton
import com.Talzere8.Qoxhex.core.ui.TrailBackground

private const val TUTORIAL_STEPS = 3

@Composable
fun TutorialPanel(onMenu: () -> Unit) {
    var step by rememberSaveable { mutableIntStateOf(0) }
    val stepText = when (step) {
        0 -> stringResource(R.string.tutorial_step_1)
        1 -> stringResource(R.string.tutorial_step_2)
        else -> stringResource(R.string.tutorial_step_3)
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
                title = stringResource(R.string.tutorial_title),
                subtitle = stringResource(R.string.tutorial_subtitle)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                border = BorderStroke(1.dp, SurfaceDeep),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    StepDiagram(step = step)
                    Text(
                        text = stepText,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Graphite
                    )
                    Text(
                        text = stringResource(
                            R.string.tutorial_step_counter,
                            step + 1,
                            TUTORIAL_STEPS
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            ProgressRail(filled = step + 1, total = TUTORIAL_STEPS, fill = Cobalt, track = SurfaceDeep)
            Spacer(modifier = Modifier.height(18.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                SecondaryButton(
                    text = stringResource(R.string.back),
                    onClick = { if (step > 0) step -= 1 },
                    enabled = step > 0,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text = stringResource(R.string.next),
                    onClick = { if (step < TUTORIAL_STEPS - 1) step += 1 },
                    enabled = step < TUTORIAL_STEPS - 1,
                    modifier = Modifier.weight(1f)
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
private fun StepDiagram(step: Int) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        val top = size.height * 0.15f
        val bottom = size.height * 0.85f
        val midX = size.width / 2f
        val spread = size.width * 0.26f
        drawCircle(
            color = Cobalt,
            radius = 10.dp.toPx(),
            center = Offset(midX, top)
        )
        drawLine(
            color = if (step == 2) SurfaceDeep else Cobalt,
            start = Offset(midX, top),
            end = Offset(midX - spread, bottom),
            strokeWidth = 6.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = Cobalt,
            start = Offset(midX, top),
            end = Offset(midX + spread, bottom),
            strokeWidth = 6.dp.toPx(),
            cap = StrokeCap.Round
        )
        if (step == 2) {
            for (mark in 1..3) {
                val t = mark / 4f
                val px = midX + (midX - spread - midX) * t
                val py = top + (bottom - top) * t
                drawLine(
                    color = Orange,
                    start = Offset(px - 9.dp.toPx(), py - 9.dp.toPx()),
                    end = Offset(px + 9.dp.toPx(), py + 9.dp.toPx()),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
        val bayY = bottom + 4.dp.toPx()
        for (slot in 0 until 3) {
            val bx = midX + (slot - 1) * spread
            drawRoundRect(
                color = if (step == 1 && slot == 2) Orange else SurfaceDeep,
                topLeft = Offset(bx - 22.dp.toPx(), bayY),
                size = androidx.compose.ui.geometry.Size(44.dp.toPx(), 14.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
            )
        }
    }
}
