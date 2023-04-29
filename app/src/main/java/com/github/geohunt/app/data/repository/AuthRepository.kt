package com.github.geohunt.app.data.repository

import androidx.activity.ComponentActivity
import com.firebase.ui.auth.AuthUI
import com.github.geohunt.app.data.exceptions.auth.UserNotLoggedInException
import com.github.geohunt.app.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

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
     * Returns the currently authenticated user as it is stored in Firebase Auth.
     *
     * The caller must first check that there is a user logged in by [requireLoggedIn], otherwise
     * this function will fail with a [NullPointerException].
     *
     *
     * @return a Firebase user, converted to the external model.
     */
    @Deprecated("If you use this function, you very probably want the user as it is" +
            "defined in the RTDB and not in Firebase Auth. Consider using `UserRepository.getCurrentUser()`.")
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
