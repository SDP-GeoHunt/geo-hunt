package com.github.geohunt.app.settings

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.github.geohunt.app.model.DataPool


interface SettingsStore {
    val theme: Setting<ThemeSetting>

    companion object {

        private val stores = DataPool<Context, BasicSettingsStore> {
            return@DataPool BasicSettingsStore(it)
        }

        fun get(context: Context): BasicSettingsStore { return stores.get(context) }

        @Composable
        fun get(): BasicSettingsStore { return stores.get(LocalContext.current) }
    }
}