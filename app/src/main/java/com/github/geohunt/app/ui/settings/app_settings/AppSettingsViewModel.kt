package com.github.geohunt.app.ui.settings.app_settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.geohunt.app.data.repository.AppSettingsRepository
import com.github.geohunt.app.data.settings.Theme
import kotlinx.coroutines.launch

/**
 * The view model for the app settings page.
 */
class AppSettingsViewModel(appSettingsRepository: AppSettingsRepository): ViewModel() {

    private val _themeSetting = appSettingsRepository.themeSetting
    val theme = _themeSetting.toStateFlow(viewModelScope)

    fun setTheme(theme: Theme) {
        viewModelScope.launch {
            _themeSetting.setter(theme)
        }
    }
}