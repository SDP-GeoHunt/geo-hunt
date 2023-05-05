package com.github.geohunt.app.ui.settings.app_settings

import com.github.geohunt.app.data.repository.AppSettingsRepository
import com.github.geohunt.app.data.settings.AppSetting
import com.github.geohunt.app.data.settings.Theme
import kotlinx.coroutines.flow.flowOf
import org.junit.Test
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class AppSettingsViewModelTest {

    @Test
    fun initialThemeIsCorrect() {
        var viewModel = AppSettingsViewModel(mockSettingRepository(Theme.SYSTEM))
        assert(viewModel.theme.value == Theme.SYSTEM)
        viewModel = AppSettingsViewModel(mockSettingRepository(Theme.DARK))
        assert(viewModel.theme.value == Theme.DARK)
    }

    @Test
    fun updatesCorrectly() {
        val cf = CompletableFuture<Theme>()
        val viewModel = AppSettingsViewModel(mockSettingRepository(Theme.SYSTEM) {
            cf.complete(it)
        })
        assert(viewModel.theme.value == Theme.SYSTEM)
        viewModel.setTheme(Theme.DARK)
        assert(cf.get(2, TimeUnit.SECONDS) == Theme.DARK)
    }

    private fun mockSettingRepository(theme: Theme, onSet: suspend (Theme) -> Any = {}): AppSettingsRepository {
        return object: AppSettingsRepository {
            override val themeSetting: AppSetting<Theme>
                get() = AppSetting(flowOf(), theme) { onSet(it) }
        }
    }
}