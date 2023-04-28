package com.github.geohunt.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.github.geohunt.app.data.repository.AppContainer
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
    private lateinit var container: AppContainer
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        container = AppContainer.getInstance(application)
        viewModel = LoginViewModel(container.auth, container.user)

        val loginLauncher = viewModel.registerLoginPrompt(
            this@LoginActivity,
            onSuccess = { response ->
                viewModel.acceptResponse(response, andThen = { onSuccessfulLogin() })
            },
            onFailure = {
                Log.e("auth", "Authentication failed with error $it")
            }
        )

        // Check that the user is not already logged in
        if (viewModel.isLoggedIn()) {
            onSuccessfulLogin()
        }

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