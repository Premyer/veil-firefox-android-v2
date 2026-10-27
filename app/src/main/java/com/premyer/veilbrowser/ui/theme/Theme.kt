package com.premyer.veilbrowser

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF7AA2FF),
    secondary = Color(0xFF8AE7C3),
    tertiary = Color(0xFFB39DDB),
    background = Color(0xFF101418),
    surface = Color(0xFF1B222B),
    surfaceVariant = Color(0xFF2A3542),
    onPrimary = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFFE2E8F0)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1F6FEB),
    secondary = Color(0xFF2EB67D),
    tertiary = Color(0xFF7C4DFF),
    background = Color(0xFFF5F7FB),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE7ECF4),
    onPrimary = Color.White,
    onSurface = Color(0xFF0F172A),
    onSurfaceVariant = Color(0xFF475569)
)

@Composable
fun VeilBrowserTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
