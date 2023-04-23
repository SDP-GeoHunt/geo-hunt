package com.github.geohunt.app.ui.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import com.github.geohunt.app.settings.SettingsStore
import com.github.geohunt.app.settings.ThemeSetting
import com.github.geohunt.app.utility.rememberSetting


private val LightColors = lightColors(
    primary = md_theme_light_primary,
    primaryVariant = md_theme_dark_onPrimary,
    onPrimary = md_theme_light_onPrimary,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    error = md_theme_light_error,
    onError = md_theme_light_onError,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface
)


@SuppressLint("ConflictingOnColor")
private val DarkColors = darkColors(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    error = md_theme_dark_error,
    onError = md_theme_dark_onError,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface
)

@Composable
fun GeoHuntTheme(
    content: @Composable() () -> Unit
) {
    val themeSettings = rememberSetting(SettingsStore.get().theme)
    val useDarkTheme: Boolean = when(themeSettings.value) {
        ThemeSetting.SYSTEM -> isSystemInDarkTheme()
        ThemeSetting.DARK -> true
        ThemeSetting.LIGHT -> false
    }

    val colors = if (!useDarkTheme) {
        LightColors
    } else {
        DarkColors
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}