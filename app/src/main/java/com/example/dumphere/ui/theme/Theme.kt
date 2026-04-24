package com.example.dumphere.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Sand,
    secondary = Caramel,
    tertiary = Blush,
    background = MochaDark,
    surface = Mocha,
    onPrimary = MochaDark,
    onSecondary = WarmWhite,
    onTertiary = MochaDark,
    onBackground = WarmWhite,
    onSurface = WarmWhite
)

private val LightColorScheme = lightColorScheme(
    primary = Mocha,
    secondary = Caramel,
    tertiary = Sand,
    background = Cream,
    surface = WarmWhite,
    onPrimary = WarmWhite,
    onSecondary = WarmWhite,
    onTertiary = MochaDark,
    onBackground = MochaDark,
    onSurface = MochaDark
)

@Composable
fun DumpHereTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
