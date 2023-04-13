package com.github.geohunt.app.mocks

import androidx.activity.ComponentActivity
import com.github.geohunt.app.authentication.Authenticator
import com.github.geohunt.app.model.database.api.User
import java.util.concurrent.CompletableFuture

class MockAuthenticator(override val user: User?,
                        val authenticateCb: (a: ComponentActivity) -> CompletableFuture<User> = {
                            CompletableFuture.completedFuture(null)
                        }) : Authenticator {
    override fun authenticate(activity: ComponentActivity): CompletableFuture<User> {
        return authenticateCb(activity)
    }

    override fun signOut(activity: ComponentActivity): CompletableFuture<Void> {
        TODO("Not yet implemented")
    }
}