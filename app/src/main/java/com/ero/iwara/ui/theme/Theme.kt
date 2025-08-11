package com.ero.iwara.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColorScheme(
    primary = Color.Gray,
    primaryContainer = Color.DarkGray,
    secondary = Color.LightGray,
    secondaryContainer = Color.Black
)

private val LightColorPalette = lightColorScheme(
    primary = TEAL300,
    primaryContainer = TEAL700,
    secondary = Color(0xFF64FFDA),
    secondaryContainer = Color(0xFF1DE9B6),
)

@Composable
fun IwaraTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}