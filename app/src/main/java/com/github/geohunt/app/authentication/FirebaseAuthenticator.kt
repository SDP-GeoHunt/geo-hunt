package com.github.geohunt.app.authentication

import android.app.Activity.RESULT_OK
import androidx.activity.ComponentActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.github.geohunt.app.BuildConfig
import com.github.geohunt.app.R
import com.github.geohunt.app.model.database.api.Database
import java.util.concurrent.CompletableFuture
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.model.database.api.UserNotFoundException

/**
 * Implements the Authenticator for Firebase.
 */
class FirebaseAuthenticator(
    private var auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private var authUi: AuthUI = AuthUI.getInstance()
) : Authenticator {

    override val user: User? get() = auth.currentUser?.let {
            FirebaseUserAdapter(it)
        }

    override fun authenticate(activity: ComponentActivity): CompletableFuture<User> {
        if (user != null) return CompletableFuture.completedFuture(user)

        val completableFuture = CompletableFuture<User>()
        val signInLauncher = activity.registerForActivityResult(
            FirebaseAuthUIActivityResultContract())
        { res ->
            if (res.resultCode == RESULT_OK) {
                if (user == null) {
                    completableFuture.completeExceptionally(IllegalStateException("Auth returns that user is not logged in, despite a successful connection."))
                } else {
                    // insert into database if not
                    insertIntoDatabase(activity, completableFuture)
                }

                user?.let { Database.databaseFactory.get()(activity).insertNewUser(it) }
            } else
                completableFuture.completeExceptionally(res.idpResponse?.error)
        }

        val signInIntent = authUi
            .createSignInIntentBuilder()
            .setIsSmartLockEnabled(!BuildConfig.DEBUG /* credentials */, true /* hints */)
            .setAvailableProviders(arrayListOf(
                AuthUI.IdpConfig.GoogleBuilder().build(),
                AuthUI.IdpConfig.EmailBuilder().build()
            ))
            .setTheme(R.style.GeoHunt)
            .build()

        signInLauncher.launch(signInIntent)
        return completableFuture
    }

    private fun insertIntoDatabase(
        activity: ComponentActivity,
        completableFuture: CompletableFuture<User>,
    ) {
        val db = Database.databaseFactory.get()(activity)
        user?.let {user ->
            // Check if user is not found.
            db.getUserById(user.uid).fetch().addOnFailureListener {
                when(it) {
                    // If so, create it
                    is UserNotFoundException -> db.insertNewUser(user)
                        .addOnCompleteListener { completableFuture.complete(user) }
                        .addOnFailureListener { completableFuture.completeExceptionally(it) }
                    else -> throw IllegalStateException("Unexpected response when fetching user $it")
                }
            }.addOnCompleteListener {
                // otherwise, ignore it
                completableFuture.complete(user)
            }
        }

    }

    override fun signOut(activity: ComponentActivity): CompletableFuture<Void> {
        if (user == null) return CompletableFuture.completedFuture(null)

        val cf = CompletableFuture<Void>()

        authUi.signOut(activity).addOnCompleteListener { cf.complete(null) }
        return cf
    }
}