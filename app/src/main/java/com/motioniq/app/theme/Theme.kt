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
    primary = KineticGreen,
    onPrimary = BrandNavy,
    primaryContainer = SurfaceDark2,
    onPrimaryContainer = KineticGreen,
    secondary = ElectricBlue,
    onSecondary = BrandNavy,
    secondaryContainer = DeepIndigo.copy(alpha = 0.3f),
    onSecondaryContainer = ElectricBlue,
    tertiary = PulseOrange,
    onTertiary = Color.White,
    background = BrandNavy,
    onBackground = TextHighDark,
    surface = SurfaceDark1,
    onSurface = TextHighDark,
    surfaceVariant = CardDarkElevated,
    onSurfaceVariant = TextMediumDark,
    outline = TextLowDark,
    error = Color(0xFFEF4444),
    onError = Color.White
  )

private val LightColorScheme =
  lightColorScheme(
    primary = BrandNavy,
    onPrimary = Color.White,
    primaryContainer = SoftTileBlue,
    onPrimaryContainer = BrandNavy,
    secondary = ElectricBlue,
    onSecondary = Color.White,
    secondaryContainer = SoftTileCyan,
    onSecondaryContainer = TealEnergy,
    tertiary = PulseOrange,
    onTertiary = Color.White,
    background = BackgroundLight,
    onBackground = TextHighLight,
    surface = SurfaceLight,
    onSurface = TextHighLight,
    surfaceVariant = CardLightElevated,
    onSurfaceVariant = TextMediumLight,
    outline = TextLowLight,
    error = Color(0xFFEF4444),
    onError = Color.White
  )

@Composable
fun MOTIONIQTheme(
  darkTheme: Boolean = true,
  // MOTIONIQ uses its own signature athletic kinetic palette
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
