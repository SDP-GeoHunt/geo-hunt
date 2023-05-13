package com.github.geohunt.app.ui.screens.teamprogress

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.geohunt.app.data.repository.AppContainer
import com.github.geohunt.app.data.repository.AuthRepositoryInterface
import com.github.geohunt.app.data.repository.UserRepositoryInterface
import com.github.geohunt.app.model.User
import com.github.geohunt.app.ui.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeamProgressViewModel(
    override val authRepository: AuthRepositoryInterface,
    val userRepository: UserRepositoryInterface,
): AuthViewModel(authRepository) {
    private val _teamMembers = mutableStateListOf<User?>(null, null, null)
    val teamMembers: List<User?> = _teamMembers

    private val _newMessages: MutableStateFlow<Int> = MutableStateFlow(0)
    val newMessages: StateFlow<Int> = _newMessages.asStateFlow()

    private fun fetchTeamMembers() {
        viewModelScope.launch {
            val currentUser = User(
                id = "test",
                displayName = "Test user",
                profilePictureUrl = "https://cdn.swisscows.com/image?url=https%3A%2F%2Ftse4.mm.bing.net%2Fth%3Fid%3DOIP.K3fwkpzk7mXxBv8VvwRALAHaLH%26pid%3DApi"
            )
            delay(2000)
            Log.i("teamMembers", "Got member: $currentUser")
            for (i in 0..2) {
                _teamMembers[i] = currentUser
                delay(500)
            }
        }
    }

    init {
        fetchTeamMembers()
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                val container = AppContainer.getInstance(application)

                TeamProgressViewModel(
                    container.auth,
                    container.user
                )
            }
        }
    }
}