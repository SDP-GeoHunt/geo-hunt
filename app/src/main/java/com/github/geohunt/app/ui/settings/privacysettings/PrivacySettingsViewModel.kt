package com.github.geohunt.app.ui.settings.privacysettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.geohunt.app.data.repository.ProfileVisibilityRepositoryInterface
import com.github.geohunt.app.data.repository.UserRepository
import com.github.geohunt.app.model.database.api.ProfileVisibility
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PrivacySettingsViewModel(
    private val userRepository: UserRepository,
    private val profileVisibilityRepository: ProfileVisibilityRepositoryInterface
): ViewModel() {
    private val _isDisabled = MutableStateFlow(true)
    val isDisabled = _isDisabled.asStateFlow()

    private val _profileVisibility = MutableStateFlow<ProfileVisibility?>(null)
    val profileVisibility = _profileVisibility.asStateFlow()

    private lateinit var uid: String

    init {
        viewModelScope.launch {
            uid = userRepository.getCurrentUser().id
            profileVisibilityRepository.getProfileVisibility(uid).collect {
                _isDisabled.value = false
                _profileVisibility.value = it
            }
        }
    }

    /**
     * Sets the profile visibility on the view model and reflects changes
     * on the database.
     */
    fun setProfileVisibility(profileVisibility: ProfileVisibility) {
        viewModelScope.launch {
            _isDisabled.value = true
            profileVisibilityRepository.setProfileVisibility(uid, profileVisibility)
        }
    }
}