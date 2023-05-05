package com.github.geohunt.app.data.repository

import com.github.geohunt.app.data.settings.AppSetting
import com.github.geohunt.app.data.settings.Theme

interface AppSettingsRepository {
    val themeSetting: AppSetting<Theme>
}