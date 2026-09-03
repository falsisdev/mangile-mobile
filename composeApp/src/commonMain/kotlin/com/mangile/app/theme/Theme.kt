package com.mangile.app.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

val MangileGreen = Color(0xFF00C853)
val MangileIndigo = Color(0xFF3D5AFE)
val MangileRose = Color(0xFFFF1744)
val MangileAmber = Color(0xFFFF9100)
val MangileBlue = Color(0xFF2979FF)

enum class ThemeMode(val label: String) {
    SYSTEM("Sistem"),
    LIGHT("Aydınlık"),
    DARK("Karanlık")
}

enum class ThemeColor(val color: Color, val label: String) {
    DYNAMIC(Color.Transparent, "Cihaz"),
    GREEN(MangileGreen, "Zümrüt"),
    INDIGO(MangileIndigo, "İndigo"),
    ROSE(MangileRose, "Gül"),
    AMBER(MangileAmber, "Kehribar"),
    BLUE(MangileBlue, "Okyanus")
}

class AppThemeState {
    var themeMode by mutableStateOf(ThemeMode.SYSTEM)
    var themeColor by mutableStateOf(ThemeColor.DYNAMIC)
}

val LocalAppTheme = compositionLocalOf { AppThemeState() }

@Composable
fun MangileTheme(
    appThemeState: AppThemeState = LocalAppTheme.current,
    content: @Composable () -> Unit
) {
    val darkTheme = when (appThemeState.themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val primaryColor = appThemeState.themeColor.color

    val dynamicScheme = if (appThemeState.themeColor == ThemeColor.DYNAMIC) getDynamicColorScheme(darkTheme) else null

    val colorScheme = dynamicScheme ?: if (darkTheme) {
        darkColorScheme(
            primary = primaryColor,
            onPrimary = Color.Black,
            primaryContainer = primaryColor.copy(alpha = 0.3f),
            onPrimaryContainer = primaryColor.copy(alpha = 0.9f),
            secondary = MangileIndigo,
            background = Color(0xFF121212),
            surface = Color(0xFF1E1E1E),
            surfaceVariant = Color(0xFF2C2C2C),
            onSurface = Color(0xFFE0E0E0),
            onSurfaceVariant = Color(0xFFAAAAAA),
            outline = Color(0xFF424242),
            outlineVariant = Color(0xFF2C2C2C)
        )
    } else {
        lightColorScheme(
            primary = primaryColor,
            onPrimary = Color.White,
            primaryContainer = primaryColor.copy(alpha = 0.2f),
            onPrimaryContainer = primaryColor,
            secondary = MangileIndigo,
            background = Color(0xFFF8F9FA),
            surface = Color.White,
            surfaceVariant = Color(0xFFF0F2F5),
            onSurface = Color(0xFF1C1B1F),
            onSurfaceVariant = Color(0xFF49454F),
            outline = Color(0xFFE0E0E0),
            outlineVariant = Color(0xFFF0F2F5)
        )
    }

    CompositionLocalProvider(LocalAppTheme provides appThemeState) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography(
                headlineLarge = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                titleLarge = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                titleMedium = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            ),
            content = content
        )
    }
}
