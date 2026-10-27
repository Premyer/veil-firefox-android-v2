package com.premyer.veilfirefoxandroidv2

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF7AA2FF),
    secondary = Color(0xFF8AE7C3),
    tertiary = Color(0xFFB39DDB),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2F5BFF),
    secondary = Color(0xFF1FB88A),
    tertiary = Color(0xFF7C4DFF),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFF3F5F9)
)

@Composable
fun VeilFirefoxAndroidV2Theme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
