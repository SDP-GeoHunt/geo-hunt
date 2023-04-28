package com.github.geohunt.app.ui.settings.privacysettings

import androidx.lifecycle.ViewModel
import com.github.geohunt.app.model.database.api.ProfileVisibility
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PrivacySettingsViewModel(): ViewModel() {
    private val _profileVisibility = MutableStateFlow(ProfileVisibility.PUBLIC)
    val profileVisibility: StateFlow<ProfileVisibility> = _profileVisibility

    /**
     * Sets the profile visibility on the view model and reflects changes
     * on the database.
     */
    fun setProfileVisibility(profileVisibility: ProfileVisibility) {
        _profileVisibility.value = profileVisibility
        // TODO("Make changes in the database")
    }
}