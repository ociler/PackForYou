package com.packforyou.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.material.darkColors
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.material.MaterialTheme as MaterialTheme2

private val DarkColorPalette = darkColors(
    primary = PurpleMain,
    primaryVariant = PurpleMain,
    secondary = Color.Gray,
    background = elevation00,
    surface = elevation01,
    error = Red
)

private val DarkColorScheme = darkColorScheme(
    primary = PurpleMain,
    error = Red,
    background = elevation00,
    onBackground = Color.White,
    onError = Color.White,
    onPrimary = Color.Black,
    secondary = Color.Gray,
    onSecondary = Color.Black,
    onPrimaryContainer = Color.Black,
    tertiary = Citron,
    onTertiary = Color.White,
    surface = elevation16
)

@Composable
fun MyGymPlusTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = DarkColorScheme
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(color = Color.DarkGray)
    MaterialTheme2(
        colorScheme = colors,
        typography = typography,
        content = content
    )
}

@Composable
fun MyGymPlusTheme2(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = DarkColorPalette
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(color = Color.DarkGray)
    MaterialTheme2(
        colors = colors,
        typography = typography2,
        shapes = shapes,
        content = content
    )
}