package com.github.geohunt.app.ui.settings.app_settings

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.github.geohunt.app.data.repository.AppSettingsRepository
import com.github.geohunt.app.data.settings.AppSetting
import com.github.geohunt.app.data.settings.Theme
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class AppSettingsPageTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showsTheCorrectThemeForSystem() {
        checkTheme(Theme.SYSTEM, "System")
    }

    @Test
    fun showsTheCorrectThemeForDark() {
        checkTheme(Theme.DARK, "Dark")
    }

    @Test
    fun showsTheCorrectThemeForLight() {
        checkTheme(Theme.LIGHT, "Light")
    }

    private fun checkTheme(theme: Theme, str: String) {
        composeTestRule.setContent {
            AppSettingsPage(onBack = { }, viewModel = createViewModel(theme))
        }
        val ts = composeTestRule.onNodeWithTag("settings-theme")
        ts.assertExists()
        composeTestRule.onNodeWithText(str).assertExists()
    }

    private fun createViewModel(theme: Theme): AppSettingsViewModel {
        return AppSettingsViewModel(mockSettingRepository(theme))
    }

    private fun mockSettingRepository(theme: Theme): AppSettingsRepository {
        return object: AppSettingsRepository {
            override val themeSetting: AppSetting<Theme>
                get() = AppSetting(flowOf(), theme) {}

        }
    }
}