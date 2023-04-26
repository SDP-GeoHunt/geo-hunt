package com.github.geohunt.app.data.repository

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import androidx.activity.ComponentActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.FirebaseUiException
import com.firebase.ui.auth.IdpResponse
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.github.geohunt.app.BuildConfig
import com.github.geohunt.app.R
import com.github.geohunt.app.data.exceptions.auth.AuthenticationCancelledException
import com.github.geohunt.app.data.exceptions.auth.AuthenticationFailureException
import com.github.geohunt.app.data.exceptions.auth.UserAlreadyAuthenticatedException
import com.github.geohunt.app.data.exceptions.auth.UserNotLoggedInException
import com.github.geohunt.app.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Contains methods related to authenticating the user, including creation, update and deletion
 * of users.
 *
 * The repository is designed to present an interface independent from Firebase Auth's methods;
 * as such, it is framework agnostic, so that Firebase Auth could be replaced easily.
 *
 * Some methods from other repositories can rely on the user being authenticated through the
 * [requireLoggedIn] assertion. In this case, [AuthRepository] is injected as a dependency of the
 * repository for easier mocking.
 */
class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val authUi: AuthUI = AuthUI.getInstance()
) {
    /**
     * Returns the list of identity providers the user may choose to authenticate itself.
     */
    private val providers: List<IdpConfig> = listOf(
        IdpConfig.GoogleBuilder().build(),
        IdpConfig.EmailBuilder().build()
    )

    /**
     * Launches the authentication activity, and returns the result of the identity provider selected
     * by the user (e.g. Google, Facebook, e-mail, etc.)
     *
     * @param activity The activity creating the intent.
     * @return the Identity Provider (IDP) response.
     * @throws [FirebaseUiException] if Firebase UI failed, [AuthenticationCancelledException] if
     *         the user cancelled the authentication, or [AuthenticationFailureException] for
     *         undetermined exceptions.
     */
    @Throws(FirebaseUiException::class, AuthenticationCancelledException::class, AuthenticationFailureException::class)
    private suspend fun launchAuthPrompt(activity: ComponentActivity): IdpResponse {
        return suspendCoroutine { continuation ->
            // Listen for the activity result
            val signInLauncher = activity.registerForActivityResult(
                FirebaseAuthUIActivityResultContract()
            ) { res: FirebaseAuthUIAuthenticationResult ->
                if (res.resultCode == RESULT_OK && res.idpResponse?.error == null) {
                    continuation.resume(res.idpResponse!!)
                } else {
                    continuation.resumeWithException(
                        if (res.resultCode == RESULT_CANCELED) AuthenticationCancelledException()
                        else res.idpResponse?.error ?: AuthenticationFailureException()
                    )
                }
            }

            // Launch the activity
            val signInIntent = authUi
                .createSignInIntentBuilder()
                .setIsSmartLockEnabled(!BuildConfig.DEBUG /* credentials */, true /* hints */)
                .setAvailableProviders(providers)
                .setTheme(R.style.GeoHunt)
                .build()

            signInLauncher.launch(signInIntent)
        }
    }

    /**
     * Tries authenticating the user by launching a prompt. The user may choose its preferred
     * authentication methods among the given [providers]. If the authentication fails, returns null.
     *
     * If the user is already logged in, this method throws an [UserAlreadyAuthenticatedException].
     *
     * @see [launchAuthPrompt]
     */
    @Throws(UserAlreadyAuthenticatedException::class)
    suspend fun promptAuthentication(activity: ComponentActivity): IdpResponse? {
        if (!isLoggedIn()) {
            return try {
                launchAuthPrompt(activity)
            } catch (_: Exception) {
                null
            }
        }
        throw UserAlreadyAuthenticatedException()
    }

    /**
     * Returns the currently authenticated user.
     *
     * The caller must first check that there is a user logged in by [requireLoggedIn], otherwise
     * this function will fail with a [NullPointerException].
     *
     * @return a Firebase user, converted to the external model.
     */
    fun getCurrentUser(): User {
        val currentUser = auth.currentUser!!
        return User(
            id = currentUser.uid,
            displayName = currentUser.displayName,
            profilePictureUrl = currentUser.photoUrl.toString()
        )
    }

    /**
     * Returns true if there is an authenticated user.
     *
     * @see requireLoggedIn
     */
    fun isLoggedIn(): Boolean = auth.currentUser != null

    /**
     * Ensures that the user is logged in, or throws a [UserNotLoggedInException] otherwise.
     *
     * @see isLoggedIn
     */
    @Throws(UserNotLoggedInException::class)
    fun requireLoggedIn() {
        if (!isLoggedIn()) {
            throw UserNotLoggedInException()
        }
    }

    /**
     * Logs out the currently authenticated user.
     *
     * If there is no currently authenticated user, this method is a nop.
     *
     * @param context The context requesting the log out.
     */
    suspend fun logOut(context: ComponentActivity) {
        if (isLoggedIn()) {
            authUi.signOut(context).await()
        }
    }
}
