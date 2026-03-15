package com.pabloku.picalertsapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    secondary = PrimaryBlueLight,
    tertiary = BorderBlue,
    background = TextPrimary,
    surface = TextPrimary,
    onPrimary = SurfaceWhite,
    onSecondary = SurfaceWhite,
    onTertiary = SurfaceWhite,
    onBackground = SurfaceWhite,
    onSurface = SurfaceWhite
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    secondary = PrimaryBlueLight,
    tertiary = BorderBlue,
    background = ScreenBackground,
    surface = SurfaceWhite,
    onPrimary = SurfaceWhite,
    onSecondary = TextPrimary,
    onTertiary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = ErrorRed,
    onError = SurfaceWhite
)

@Composable
fun PicAlertsAppTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
