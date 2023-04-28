package com.github.geohunt.app.ui.settings.app_settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.geohunt.app.data.repository.AppSettingsRepository
import com.github.geohunt.app.data.settings.Theme
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * The view model for the app settings page.
 */
class AppSettingsViewModel(appSettingsRepository: AppSettingsRepository): ViewModel() {

    private val _themeSetting = appSettingsRepository.themeSetting
    private val _theme = _themeSetting.toOneWayMutableStateFlow(viewModelScope)
    val theme: StateFlow<Theme> = _theme.asStateFlow()

    fun setTheme(theme: Theme) {
        viewModelScope.launch {
            _themeSetting.setter(theme)
        }
    }
}