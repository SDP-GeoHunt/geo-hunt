package com.github.geohunt.app.ui

import androidx.lifecycle.ViewModel
import com.github.geohunt.app.data.repository.AuthRepository

/**
 * Base [ViewModel] for activities that rely on authentication.
 */
open class AuthViewModel(
    open val authRepository: AuthRepository
): ViewModel() {
    /**
     * Returns true if the user is currently logged in.
     */
    fun isLoggedIn() = authRepository.isLoggedIn()
}