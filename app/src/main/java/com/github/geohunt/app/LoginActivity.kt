package com.github.geohunt.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.github.geohunt.app.ui.screens.GeoHuntScreen
import com.github.geohunt.app.ui.screens.login.LoginScreen
import com.github.geohunt.app.ui.screens.login.LoginViewModel
import com.github.geohunt.app.utility.replaceActivity

/**
 * Main entry point of the application.
 *
 * If users are not logged in, they are asked to do by clicking on the "Sign in" button, which
 * launches an authentication prompt.
 *
 * If they are already logged in, they are simply redirected to the [MainActivity].
 *
 * @see [LoginViewModel.login]
 */
class LoginActivity : ComponentActivity() {
    private val viewModel: LoginViewModel by viewModels(factoryProducer = { LoginViewModel.Factory })

    private val PREFERENCES_FILE = "preferences"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check that the user is not already logged in
        if (viewModel.isLoggedIn()) {
            onSuccessfulLogin()
        }
        val authenticator: Authenticator = Authenticator.authInstance.get()
        val settings = getSharedPreferences(PREFERENCES_FILE, 0)

        // Check if the application is being launched for first time
        // and if so, display the tutorial
        if (settings.getBoolean("first_application_open", true)) {
            val intent = Intent(this@LoginActivity, TutorialActivity::class.java)
            replaceActivity(intent)
            settings.edit().putBoolean("first_application_open", false).apply()
        }

        authenticator.user?.let { loggedIn() }

        // Register the login launcher
        val loginLauncher = viewModel.registerLoginPrompt(
            this@LoginActivity,
            onSuccess = { response ->
                viewModel.acceptResponse(response, andThen = { onSuccessfulLogin() })
            },
            onFailure = {
                Log.e("auth", "Authentication failed with error $it")
            }
        )

        setContent {
            GeoHuntScreen {
                LoginScreen(onSignInClick = { viewModel.launchLoginPrompt(loginLauncher) })
            }
        }
    }

    private fun onSuccessfulLogin() {
        // Prevent the user from going back to the login activity by destroying it
        replaceActivity(Intent(this@LoginActivity, MainActivity::class.java))
    }
}