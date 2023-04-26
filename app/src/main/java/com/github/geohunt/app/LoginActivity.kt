package com.github.geohunt.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.github.geohunt.app.authentication.Authenticator
import com.github.geohunt.app.ui.screens.login.LoginScreen
import com.github.geohunt.app.ui.theme.GeoHuntTheme

/**
 * Login activity.
 */
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authenticator: Authenticator = Authenticator.authInstance.get()

        authenticator.user?.let { loggedIn() }

        if (intent.hasExtra("login")) {
            authenticator.authenticate(this@LoginActivity).thenAccept {
                it?.let { loggedIn() }
            }
        }

        setContent {
            GeoHuntTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    LoginScreen(context = this@LoginActivity)
                }
            }
        }
    }

    private fun loggedIn() {
        // Prevent the user to go back to the login activity
        finish()
    }
}
