package com.Talzere8.Qoxhex.core.ui

import com.Talzere8.Qoxhex.domain.model.RouteColour
import com.Talzere8.Qoxhex.core.theme.Cobalt
import com.Talzere8.Qoxhex.core.theme.GraphiteSoft
import com.Talzere8.Qoxhex.core.theme.Orange
import com.Talzere8.Qoxhex.core.theme.PaperRoute
import androidx.compose.ui.graphics.Color

fun groupedNumber(value: Int): String {
    val raw = value.toString()
    val builder = StringBuilder()
    for (position in raw.indices) {
        if (position > 0 && (raw.length - position) % 3 == 0) builder.append(' ')
        builder.append(raw[position])
    }
    return builder.toString()
}

fun comboLabel(value: Float): String {
    val whole = value.toInt()
    return if (value - whole.toFloat() == 0f) whole.toString() else value.toString()
}

fun routeColourOf(colour: RouteColour): Color = when (colour) {
    RouteColour.ORANGE -> Orange
    RouteColour.COBALT -> Cobalt
    RouteColour.PAPER -> PaperRoute
    RouteColour.GRAPHITE -> GraphiteSoft
}

fun routeSpriteSlot(colour: RouteColour): Int = when (colour) {
    RouteColour.ORANGE -> 1
    RouteColour.COBALT -> 2
    RouteColour.PAPER -> 3
    RouteColour.GRAPHITE -> 4
}
