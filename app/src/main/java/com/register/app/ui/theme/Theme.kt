package com.register.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    tertiary = TertiaryDark,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceDim = SurfaceDimDark,
    secondary = Secondary,
    onError = Color.Red,
    onBackground = Color.White,
    onTertiary = Translucent,
    onPrimary = OnPrimaryDark,
    onSecondary = Color.White,
    onSecondaryContainer = BlackTranslucent
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    secondary = Secondary,
    tertiary = TertiaryLight,
    background = BackgroundLight,
    surface = SurfaceDimLight,
    surfaceDim = SurfaceDimLight,
    onPrimary = OnPrimaryLight,
    onBackground = Color.Black,
    onError = Color.Red,
    onTertiary = Translucent,
    onSecondary = Color.White,
    onSecondaryContainer = BlackTranslucent
)

@Composable
fun RegisterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}