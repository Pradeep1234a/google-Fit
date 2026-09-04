package com.motioniq.app.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = StitchCyan,
    onPrimary = StitchDarkCyan,
    primaryContainer = StitchTeal.copy(alpha = 0.35f),
    onPrimaryContainer = StitchCyan,
    secondary = StitchTeal,
    onSecondary = Color.White,
    secondaryContainer = SlateSurface2,
    onSecondaryContainer = StitchCyan,
    tertiary = PulseCoral,
    onTertiary = Color.White,
    background = SlateGround,
    onBackground = TextHighDark,
    surface = SlateSurface1,
    onSurface = TextHighDark,
    surfaceVariant = SlateSurface2,
    onSurfaceVariant = TextMediumDark,
    outline = CyanBorderSubtle,
    error = Color(0xFFEF4444),
    onError = Color.White
  )

private val LightColorScheme =
  lightColorScheme(
    primary = StitchCyan,
    onPrimary = Color.White,
    primaryContainer = SoftTileCyan,
    onPrimaryContainer = StitchDarkCyan,
    secondary = StitchTeal,
    onSecondary = Color.White,
    secondaryContainer = SoftTileBlue,
    onSecondaryContainer = StitchTeal,
    tertiary = PulseCoral,
    onTertiary = Color.White,
    background = BackgroundLight,
    onBackground = TextHighLight,
    surface = SurfaceLight,
    onSurface = TextHighLight,
    surfaceVariant = CardLightElevated,
    onSurfaceVariant = TextMediumLight,
    outline = Color(0xFFE2E8F0),
    error = Color(0xFFEF4444),
    onError = Color.White
  )

@Composable
fun MOTIONIQTheme(
  darkTheme: Boolean = true,
  // MOTIONIQ uses its signature technical kinesthetics palette from Stitch
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
