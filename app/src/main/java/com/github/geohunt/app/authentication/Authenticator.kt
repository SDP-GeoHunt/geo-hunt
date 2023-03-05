package com.github.geohunt.app.authentication

import androidx.activity.ComponentActivity
import java.util.concurrent.CompletableFuture

interface Authenticator {

    val user: User?

    /**
     * Begins an authentication phase from the given activity.
     * Returns a future that will be completed when the user will be logged in.
     * If the authentication fails, the completable future will fail.
     * @param activity The activity that requests the connection
     * @return A completable future that follows the above description
     */
    fun authenticate(activity: ComponentActivity): CompletableFuture<User>

    /**
     * Signs out the user.
     * @param activity The activity that requests the signing out
     * @return The completable future is finished when the signing out is finished.
     */
    fun signOut(activity: ComponentActivity): CompletableFuture<Void>

}