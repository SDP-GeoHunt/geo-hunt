package com.github.geohunt.app.ui.screens.login

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.FirebaseUiException
import com.firebase.ui.auth.IdpResponse
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.github.geohunt.app.BuildConfig
import com.github.geohunt.app.R
import com.github.geohunt.app.data.exceptions.auth.AuthenticationCancelledException
import com.github.geohunt.app.data.exceptions.auth.AuthenticationFailureException
import com.github.geohunt.app.data.repository.AppContainer
import com.github.geohunt.app.data.repository.AuthRepository
import com.github.geohunt.app.data.repository.UserRepository
import com.github.geohunt.app.ui.AuthViewModel
import kotlinx.coroutines.launch

/**
 * [androidx.lifecycle.ViewModel] associated to the [LoginScreen].
 *
 * Provides methods for registering and launching the login prompt, as well as processing its
 * response.
 */
class LoginViewModel(
    override val authRepository: AuthRepository,
    val userRepository: UserRepository,
    val authUi: AuthUI = AuthUI.getInstance()
): AuthViewModel(authRepository) {
    /**
     * Returns the list of identity providers the user may choose to authenticate itself.
     */
    private val providers: List<AuthUI.IdpConfig> = listOf(
        AuthUI.IdpConfig.GoogleBuilder().build(),
        AuthUI.IdpConfig.EmailBuilder().build()
    )

    /**
     * Registers the authentication launcher on the given activity.
     *
     * This must be done **unconditionally** in the activity's [Activity.onCreate] method, since the
     * activity might be destroyed and recreated. See [this link](https://developer.android.com/training/basics/intents/result#register)
     * for more information.
     */
    fun registerLoginPrompt(
        activity: ComponentActivity,
        onSuccess: (IdpResponse) -> Unit,
        onFailure: (Exception) -> Unit
    ): ActivityResultLauncher<Intent> {
        return activity.registerForActivityResult(
            FirebaseAuthUIActivityResultContract()
        ) { res: FirebaseAuthUIAuthenticationResult ->
            if (res.resultCode == Activity.RESULT_OK && res.idpResponse?.error == null) {
                onSuccess(res.idpResponse!!)
            } else {
                onFailure(
                    if (res.resultCode == Activity.RESULT_CANCELED) AuthenticationCancelledException()
                    else res.idpResponse?.error ?: AuthenticationFailureException()
                )
            }
        }
    }

    /**
     * Launches the authentication activity registered by [registerLoginPrompt]. The user may choose
     * its preferred authentication methods among the given [providers].
     *
     * @param launcher The activity launcher.
     * @throws [FirebaseUiException] if Firebase UI failed, [AuthenticationCancelledException] if
     *         the user cancelled the authentication, or [AuthenticationFailureException] for
     *         undetermined exceptions.
     */
    fun launchLoginPrompt(launcher: ActivityResultLauncher<Intent>) {
        val signInIntent = authUi
            .createSignInIntentBuilder()
            .setIsSmartLockEnabled(!BuildConfig.DEBUG /* credentials */, true /* hints */)
            .setAvailableProviders(providers)
            .setTheme(R.style.GeoHunt)
            .build()

        launcher.launch(signInIntent)
    }

    /**
     * Accepts the given Identity Provider (Idp) response and calls the given callback.
     *
     * @param response The response given by [registerLoginPrompt]'s `onSuccess` argument.
     * @param andThen The callback used after the response is correctly processed.
     */
    fun acceptResponse(response: IdpResponse, andThen: () -> Unit) {
        viewModelScope.launch {
            userRepository.createUserIfNew(response)
            andThen()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                val container = AppContainer.getInstance(application)

                LoginViewModel(
                    container.auth,
                    container.user
                )
            }
        }
    }
}