package com.github.geohunt.app.ui.components.profile.edit

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.geohunt.app.data.local.LocalPicture
import com.github.geohunt.app.data.repository.AppContainer
import com.github.geohunt.app.data.repository.AuthRepositoryInterface
import com.github.geohunt.app.data.repository.UserRepositoryInterface
import com.github.geohunt.app.model.EditedUser
import com.github.geohunt.app.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileEditPageViewModel(
    authRepository: AuthRepositoryInterface,
    private val userRepository: UserRepositoryInterface
): ViewModel() {
    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    private val _editedUser = MutableStateFlow<EditedUser?>(null)
    val editedUser = _editedUser.asStateFlow()

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating = _isUpdating.asStateFlow()

    fun update() {
        if (!_isUpdating.value) {
            _isUpdating.value = true
            viewModelScope.launch {
                _editedUser.value?.let { userRepository.updateUser(it) }
                _isUpdating.value = false
            }
        }
    }

    fun setDisplayName(dn: String) {
        _editedUser.value = _editedUser.value?.copy(newDisplayName = dn)
    }

    fun setProfilePicture(uri: Uri) {
        _editedUser.value = _editedUser.value?.copy(newProfilePicture = LocalPicture(uri))
    }

    init {
        authRepository.requireLoggedIn()

        viewModelScope.launch {
            _user.value = userRepository.getCurrentUser()
            _editedUser.value = EditedUser.fromUser(_user.value!!)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as Application
                val container = AppContainer.getInstance(application)

                ProfileEditPageViewModel(
                    container.auth,
                    container.user
                )
            }
        }
    }
}