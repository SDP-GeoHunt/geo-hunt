package com.github.geohunt.app.authentication

import android.app.Activity.RESULT_OK
import androidx.activity.ComponentActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import java.util.concurrent.CompletableFuture

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
        if (user != null) CompletableFuture.completedFuture(user)

        val completableFuture = CompletableFuture<User>()
        val signInLauncher = activity.registerForActivityResult(
            FirebaseAuthUIActivityResultContract())
        { res ->
            if (res.resultCode == RESULT_OK)
                completableFuture.complete(user)
            else
                completableFuture.completeExceptionally(res.idpResponse?.error)
        }

        val signInIntent = authUi
            .createSignInIntentBuilder()
            .setAvailableProviders(arrayListOf(
                AuthUI.IdpConfig.GoogleBuilder().build(),
                AuthUI.IdpConfig.EmailBuilder().build()
            ))
            .build()

        signInLauncher.launch(signInIntent)
        return completableFuture
    }

    override fun signOut(activity: ComponentActivity): CompletableFuture<Void> {
        if (user == null) return CompletableFuture.completedFuture(null)

        val cf = CompletableFuture<Void>()

        authUi.signOut(activity).addOnCompleteListener { cf.complete(null) }
        return cf
    }


}