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

private val DarkColorScheme =
  darkColorScheme(
    primary = PrimaryNeon,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF003814),
    onPrimaryContainer = NeonGreen,
    secondary = SecondaryCyan,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF00363D),
    onSecondaryContainer = NeonCyan,
    tertiary = TertiaryYellow,
    onTertiary = Color.Black,
    background = DarkBg,
    onBackground = LightText,
    surface = CardBg,
    onSurface = LightText,
    surfaceVariant = CardBgElevated,
    onSurfaceVariant = MutedText,
    outline = BorderDark,
    error = NeonRed,
    onError = Color.White
  )

private val LightColorScheme = DarkColorScheme

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(colorScheme = DarkColorScheme, typography = Typography, content = content)
}
