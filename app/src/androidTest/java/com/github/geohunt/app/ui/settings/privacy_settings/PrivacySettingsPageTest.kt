package com.github.geohunt.app.ui.settings.privacy_settings

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.github.geohunt.app.mocks.MockProfileVisibilityRepository
import com.github.geohunt.app.mocks.MockUserRepository
import com.github.geohunt.app.model.database.api.ProfileVisibility
import org.junit.Rule
import org.junit.Test

class PrivacySettingsPageTest {
    private fun mockViewModel(): PrivacySettingsViewModel {
        return PrivacySettingsViewModel(
            MockUserRepository(),
            MockProfileVisibilityRepository()
        )
    }

    @get:Rule
    val r = createComposeRule()

    @Test
    fun settingVisibilityUpdatesTheViewModel() {
        val vm = mockViewModel()
        r.setContent {
            PrivacySettingsPage(onBack = { }, viewModel = vm)
        }
        r.onNodeWithTag("select-following-only").performClick()
        assert(vm.profileVisibility.value == ProfileVisibility.FOLLOWING_ONLY)
        r.onNodeWithTag("select-private").performClick()
        assert(vm.profileVisibility.value == ProfileVisibility.PRIVATE)
        r.onNodeWithTag("select-public").performClick()
        assert(vm.profileVisibility.value == ProfileVisibility.PUBLIC)
    }
}