package com.mangile.app.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Mangile Brand Renkleri
val MangilePrimary = Color(0xFF10B981) // Zümrüt Yeşili
val MangilePrimaryVariant = Color(0xFF059669)
val MangileSecondary = Color(0xFF6366F1) // Indigo

// Dark Theme Palette (Mist / Zinc tabanlı)
val DarkBackground = Color(0xFF09090B)
val DarkSurface = Color(0xFF18181B)
val DarkSurfaceVariant = Color(0xFF27272A)
val DarkText = Color(0xFFFAFAFA)
val DarkTextSecondary = Color(0xFFA1A1AA)
val DarkBorder = Color(0xFF27272A)

// Light Theme Palette
val LightBackground = Color(0xFFFAFAFA)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF4F4F5)
val LightText = Color(0xFF18181B)
val LightTextSecondary = Color(0xFF71717A)
val LightBorder = Color(0xFFE4E4E7)

private val DarkColorScheme = darkColorScheme(
    primary = MangilePrimary,
    onPrimary = Color.Black,
    secondary = MangileSecondary,
    onSecondary = Color.White,
    background = DarkBackground,
    onBackground = DarkText,
    surface = DarkSurface,
    onSurface = DarkText,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = MangilePrimary,
    onPrimary = Color.White,
    secondary = MangileSecondary,
    onSecondary = Color.White,
    background = LightBackground,
    onBackground = LightText,
    surface = LightSurface,
    onSurface = LightText,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder
)

@Composable
fun MangileTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
