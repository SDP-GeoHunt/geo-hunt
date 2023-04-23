package com.github.geohunt.app.ui.components.settings.items

import androidx.compose.runtime.*
import com.github.geohunt.app.settings.SettingsStore
import com.github.geohunt.app.settings.ThemeSetting
import com.github.geohunt.app.ui.components.settings.SettingsItem
import com.github.geohunt.app.ui.components.utils.ListDropdownMenu
import com.github.geohunt.app.utility.rememberSetting

@Composable
fun ThemeSettingItem(settingsStore: SettingsStore) {
    val choice = rememberSetting(settingsStore.theme, false)

    SettingsItem(text = "Theme", {
        ListDropdownMenu(state = choice, elements = ThemeSetting.values().asList(), toString = {
            when(it) {
                ThemeSetting.SYSTEM -> "System"
                ThemeSetting.LIGHT -> "Light"
                ThemeSetting.DARK -> "Dark"
            }
        })
    })
}