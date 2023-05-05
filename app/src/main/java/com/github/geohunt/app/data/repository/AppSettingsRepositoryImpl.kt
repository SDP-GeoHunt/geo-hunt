package com.github.geohunt.app.data.repository

import androidx.datastore.core.DataStore
import com.github.geohunt.app.data.settings.AppSetting
import com.github.geohunt.app.data.settings.AppSettings
import com.github.geohunt.app.data.settings.Theme
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext


/**
 * An app settings repository.
 */
class AppSettingsRepositoryImpl(
    private val dS: DataStore<AppSettings>,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): AppSettingsRepository {
    private val defaultSettings = AppSettings()

    // Theme setting
    private val themeFlow: Flow<Theme> = dS.data.map { it.theme }
    private suspend fun setTheme(theme: Theme) {
        withContext(ioDispatcher) {
            dS.updateData { it.copy(theme = theme) }
        }
    }
    override val themeSetting = AppSetting(themeFlow, defaultSettings.theme, this::setTheme)

}