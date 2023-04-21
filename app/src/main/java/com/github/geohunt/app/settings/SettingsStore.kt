package com.github.geohunt.app.settings

import androidx.compose.runtime.MutableState


interface SettingsStore {
    val themeSettings: MutableState<ThemeSetting>
}