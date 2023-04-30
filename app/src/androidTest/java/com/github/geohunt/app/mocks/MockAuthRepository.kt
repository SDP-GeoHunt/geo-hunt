package com.github.geohunt.app.mocks

import androidx.activity.ComponentActivity
import com.github.geohunt.app.data.repository.AuthRepositoryInterface
import com.github.geohunt.app.model.User

/**
 * Represents a mocked auth repository where the user
 * is logged in as User("1", "Debug user", null)
 */
class MockAuthRepository(private val loggedUser: User = defaultLoggedUser): AuthRepositoryInterface {
    @Deprecated("If you use this and want the user as described in the RTDB, you should preferuse UserRepository#getCurrentUser()")
    override fun getCurrentUser(): User {
        return loggedUser
    }

    override fun isLoggedIn(): Boolean {
        return true
    }

    override fun requireLoggedIn() {

    }

    override suspend fun logOut(context: ComponentActivity) {

    }

    companion object {
        val defaultLoggedUser = User("1", "dn", null)
    }
}