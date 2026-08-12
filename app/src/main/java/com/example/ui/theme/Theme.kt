package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val AsteriskDarkColorScheme = darkColorScheme(
    primary = PrimaryCyan,
    onPrimary = Color(0xFF00363A),
    primaryContainer = Color(0xFF004D40),
    onPrimaryContainer = Color(0xFF80DEEA),
    secondary = SecondaryIndigo,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF312E81),
    onSecondaryContainer = Color(0xFFC7D2FE),
    tertiary = TertiaryEmerald,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = OnSurfaceText,
    surface = DarkSurface,
    onSurface = OnSurfaceText,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = OnSurfaceSubtext,
    outline = BorderOutline,
    error = AccentRed
)

private val AsteriskLightColorScheme = lightColorScheme(
    primary = Color(0xFF006875),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF97F0FF),
    onPrimaryContainer = Color(0xFF001F25),
    secondary = Color(0xFF4346A2),
    onSecondary = Color.White,
    tertiary = Color(0xFF006C4C),
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    error = AccentRed
)

@Composable
fun AsteriskTheme(
    darkTheme: Boolean = true, // Default to dark theme for cyber proxy aesthetic
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> AsteriskDarkColorScheme
        else -> AsteriskLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    AsteriskTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
