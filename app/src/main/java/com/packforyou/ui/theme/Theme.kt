package com.packforyou.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun PackForYouTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = PackForYouTypography,
        shapes = PackForYouShapes,
        content = content
    )
}

private val LightColors = lightColorScheme(
    primary = White,
    onPrimary = Grey,
    secondary = Black,
    onSecondary = Grey,
    error = Red800,
    background = White
)

private val DarkColors = darkColorScheme(
    primary = Red300,
    onPrimary = Black,
    secondary = Red300,
    onSecondary = Black,
    error = Red200
)