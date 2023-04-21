package com.github.geohunt.app.settings

import androidx.compose.runtime.MutableState
import androidx.datastore.dataStore


class BasicSettingsStore: SettingsStore {
    override val themeSettings: MutableState<ThemeSetting>
        get() = TODO("Not yet implemented")

    companion object {
         private val BasicSettingsStore.dataStore by dataStore("app-settings.json", AppSettingsSerializer)
    }
}