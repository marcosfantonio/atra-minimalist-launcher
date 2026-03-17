package com.fantonio.atra.ui.theme

import android.app.Activity
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

enum class Theme {
    LIGHT, DARK
}

private val DarkColorScheme = darkColorScheme(
    background = Color.Black,
    onBackground = Color.White,
    surface = Color.Black,
    onSurface = Color.White,
    outline = Color.DarkGray,
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    outline = Color.LightGray,
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun AtraTheme(
    theme: Theme = if (isSystemInDarkTheme()) Theme.DARK else Theme.LIGHT,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = getColorScheme(theme, dynamicColor)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
@Composable
private fun getColorScheme(theme: Theme, dynamicColor: Boolean): androidx.compose.material3.ColorScheme {
  return when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
          val context = LocalContext.current
          when (theme) {
              Theme.LIGHT -> dynamicLightColorScheme(context)
              Theme.DARK -> dynamicDarkColorScheme(context)
          }
      }
      else -> when (theme) {
          Theme.LIGHT -> LightColorScheme
          Theme.DARK -> DarkColorScheme
      }
  }
}