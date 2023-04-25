package com.github.geohunt.app.ui.components.settings.items

import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.github.geohunt.app.settings.SettingsStore
import com.github.geohunt.app.settings.ThemeSetting
import com.github.geohunt.app.ui.components.settings.DropdownSettingsItem
import com.github.geohunt.app.ui.components.utils.ListDropdownMenu
import com.github.geohunt.app.ui.rememberSetting
import com.github.geohunt.app.R

@Composable
fun ThemeSettingItem(settingsStore: SettingsStore) {
    val choice = rememberSetting(settingsStore.theme, false)

    DropdownSettingsItem(text = stringResource(id = R.string.theme), {
        val systemStr = stringResource(id = R.string.system)
        val lightStr = stringResource(id = R.string.light)
        val darkStr = stringResource(id = R.string.dark)

        ListDropdownMenu(state = choice, elements = ThemeSetting.values().asList(), toString = {
            when(it) {
                ThemeSetting.SYSTEM -> systemStr
                ThemeSetting.LIGHT -> lightStr
                ThemeSetting.DARK -> darkStr
            }
        })
    }, description = stringResource(id = R.string.theme_desc))
}