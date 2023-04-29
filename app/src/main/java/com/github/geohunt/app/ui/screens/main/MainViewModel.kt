package com.github.geohunt.app.ui.screens.main

import androidx.activity.ComponentActivity
import androidx.lifecycle.viewModelScope
import com.github.geohunt.app.data.network.NetworkMonitor
import com.github.geohunt.app.data.repository.AuthRepository
import com.github.geohunt.app.ui.AuthViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Top-level [androidx.lifecycle.ViewModel] of the app.
 *
 * This view model includes general information about the state of the app, such as network connectivity.
 *
 * For specific screen states, new view models should be created and bound to the navigation entries'
 * top-level composables in [com.github.geohunt.app.ui.components.navigation.NavigationController].
 */
class MainViewModel(
    override val authRepository: AuthRepository
) : AuthViewModel(authRepository) {
    private val networkMonitor = NetworkMonitor(Firebase.database)
    private val _isConnected = MutableStateFlow(true)

    /**
     * Returns true if the application is connected to the remote database.
     */
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    /**
     * Asks the auth repository to logout
     */
    fun logout(a: ComponentActivity, then: () -> Any) {
        viewModelScope.launch {
            authRepository.logOut(a)
            then()
        }
    }

    init {
        viewModelScope.launch {
            // Bind the UI state and the network monitor
            networkMonitor.isConnected.collect {
                _isConnected.value = it
            }
        }
    }
}