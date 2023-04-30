package com.github.geohunt.app.data.repository

import androidx.activity.ComponentActivity
import com.github.geohunt.app.data.exceptions.auth.UserNotLoggedInException
import com.github.geohunt.app.model.User

interface AuthRepositoryInterface {
    /**
     * Returns the currently authenticated user as it is stored in Firebase Auth.
     *
     * The caller must first check that there is a user logged in by [requireLoggedIn], otherwise
     * this function will fail with a [NullPointerException].
     *
     *
     * @return a Firebase user, converted to the external model.
     */
    fun getCurrentUser(): User

    /**
     * Returns true if there is an authenticated user.
     */
    fun isLoggedIn(): Boolean

    /**
     * Ensures that the user is logged in, or throws a [UserNotLoggedInException] otherwise.
     *
     * @see isLoggedIn
     */
    @Throws(UserNotLoggedInException::class)
    fun requireLoggedIn()

    /**
     * Logs out the currently authenticated user.
     *
     * If there is no currently authenticated user, this method is a nop.
     *
     * @param context The context requesting the log out.
     */
    suspend fun logOut(context: ComponentActivity)
}