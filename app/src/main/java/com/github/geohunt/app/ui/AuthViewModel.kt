package com.github.geohunt.app.ui

import androidx.lifecycle.ViewModel
import com.github.geohunt.app.data.repository.AuthRepositoryInterface

/**
 * Base [ViewModel] for activities that rely on authentication.
 */
open class AuthViewModel(
    open val authRepository: AuthRepositoryInterface
): ViewModel() {
    /**
     * Returns true if the user is currently logged in.
     */
    fun isLoggedIn() = authRepository.isLoggedIn()
}