package com.Talzere8.Qoxhex.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.Talzere8.Qoxhex.R
import com.Talzere8.Qoxhex.core.theme.CardWhite
import com.Talzere8.Qoxhex.core.theme.Graphite
import com.Talzere8.Qoxhex.core.theme.Orange
import com.Talzere8.Qoxhex.core.theme.SurfaceDeep
import com.Talzere8.Qoxhex.core.theme.TextSecondary
import com.Talzere8.Qoxhex.domain.model.Parcel
import com.Talzere8.Qoxhex.domain.model.RouteColour

@Composable
fun RouteSwatch(
    colour: RouteColour,
    showCode: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 28.dp
) {
    val fill = routeColourOf(colour)
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(8.dp))
            .background(color = fill)
            .border(2.dp, if (colour == RouteColour.PAPER) Graphite else Color.Transparent, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (showCode) {
            Text(
                text = colour.code,
                style = MaterialTheme.typography.labelSmall,
                color = if (colour == RouteColour.PAPER) Graphite else Color.White
            )
        }
    }
}

@Composable
fun ParcelSlip(
    parcel: Parcel,
    showCode: Boolean,
    modifier: Modifier = Modifier
) {
    val townText = stringResource(TrailResources.townLabel(parcel.town))
    val colourText = stringResource(TrailResources.colourLabel(parcel.colour))
    val description = stringResource(R.string.parcel_desc, colourText, townText)
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = BorderStroke(1.dp, SurfaceDeep),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier.semantics { contentDescription = description }
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(TrailResources.sprite(routeSpriteSlot(parcel.colour))),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(36.dp)
                )
                Box(modifier = Modifier.width(8.dp))
                RouteSwatch(colour = parcel.colour, showCode = showCode, size = 24.dp)
            }
            Text(
                text = townText,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                modifier = Modifier.padding(top = 6.dp)
            )
            if (parcel.rush) {
                Box(modifier = Modifier.padding(top = 4.dp)) {
                    TagChip(text = stringResource(R.string.rush_tag), accent = Orange)
                }
            }
        }
    }
}
