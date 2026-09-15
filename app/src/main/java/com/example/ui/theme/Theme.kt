package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme =
  darkColorScheme(
    primary = PureWhite,
    onPrimary = Black,
    primaryContainer = DarkCharcoal,
    onPrimaryContainer = PureWhite,
    secondary = LightBorderGray,
    onSecondary = Black,
    background = Black,
    onBackground = PureWhite,
    surface = DarkSurface,
    onSurface = PureWhite,
    surfaceVariant = DarkCharcoal,
    onSurfaceVariant = LightBorderGray,
    outline = MediumGray,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = Black,
    onPrimary = PureWhite,
    primaryContainer = LightSurfaceGray,
    onPrimaryContainer = Black,
    secondary = MediumGray,
    onSecondary = PureWhite,
    background = OffWhite,
    onBackground = Black,
    surface = PureWhite,
    onSurface = Black,
    surfaceVariant = LightSurfaceGray,
    onSurfaceVariant = DarkCharcoal,
    outline = LightBorderGray,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep monochrome white/gray/black
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

