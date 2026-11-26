package com.Talzere8.Qoxhex.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val TrailColorScheme = lightColorScheme(
    primary = Cobalt,
    onPrimary = CardWhite,
    primaryContainer = CobaltBright,
    onPrimaryContainer = CardWhite,
    secondary = Orange,
    onSecondary = Graphite,
    secondaryContainer = Orange,
    onSecondaryContainer = Graphite,
    tertiary = SuccessGreen,
    onTertiary = CardWhite,
    background = BackgroundBase,
    onBackground = Graphite,
    surface = CardWhite,
    onSurface = Graphite,
    surfaceVariant = SurfaceSoft,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceDeep,
    outlineVariant = SurfaceDeep,
    error = Orange,
    onError = CardWhite
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TrailColorScheme,
        typography = AppTypography,
        content = content
    )
}
