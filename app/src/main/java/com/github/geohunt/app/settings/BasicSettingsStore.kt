package com.github.geohunt.app.settings

import android.content.Context
import androidx.compose.runtime.*
import androidx.datastore.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class BasicSettingsStore(context: Context): SettingsStore {
    private val Context.dataStore by dataStore("app-settings.json", AppSettingsSerializer)
    private val dS = context.dataStore
    private val default = AppSettings()

    override val theme: Setting<ThemeSetting> =
        Setting(dS.data.map { it.theme }, default.theme, this::setThemeSetting)

    private suspend fun setThemeSetting(theme: ThemeSetting) {
        dS.updateData { it.copy(theme = theme) }
    }

}