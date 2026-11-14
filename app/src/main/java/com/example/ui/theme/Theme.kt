package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

private val PaperTradeDarkColorScheme =
  darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = SlateVoid,
    primaryContainer = EmeraldContainer,
    onPrimaryContainer = EmeraldBright,
    secondary = CyanAccent,
    onSecondary = SlateVoid,
    secondaryContainer = CyanContainer,
    onSecondaryContainer = CyanAccent,
    tertiary = AmberWarning,
    onTertiary = SlateVoid,
    tertiaryContainer = AmberContainer,
    onTertiaryContainer = AmberWarning,
    error = CrimsonAlert,
    onError = TextPrimary,
    errorContainer = CrimsonContainer,
    onErrorContainer = CrimsonSoft,
    background = SlateVoid,
    onBackground = TextPrimary,
    surface = SlateSurface,
    onSurface = TextPrimary,
    surfaceVariant = SlateSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    surfaceContainerLow = SlateSurface,
    surfaceContainer = SlateSurfaceElevated,
    surfaceContainerHigh = SlateSurfaceHigh,
    outline = SlateOutline,
    outlineVariant = SlateOutline,
  )

private val PaperTradeShapes =
  Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
  )

@Composable
fun PaperTradeTheme(content: @Composable () -> Unit) {
  MaterialTheme(
    colorScheme = PaperTradeDarkColorScheme,
    typography = PaperTradeTypography,
    shapes = PaperTradeShapes,
    content = content,
  )
}
