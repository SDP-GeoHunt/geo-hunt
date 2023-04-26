package com.github.geohunt.app

import android.content.Intent
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
import com.github.geohunt.app.utility.replaceActivity

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
        // prevent the user to go back to the login activity
        // so we properly replace it
        replaceActivity(Intent(this@LoginActivity, MainActivity::class.java))
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun LoginScreen(context: Context) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            textAlign = TextAlign.Center,
            style = TextStyle(
                brush = Brush.linearGradient(listOf(md_theme_light_primary, seed))
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(modifier = Modifier.testTag("signin-btn"), onClick = {
            val intent = Intent(context, LoginActivity::class.java)
            intent.putExtra("login", 1)
            context.startActivity(intent)
        }) {
            Text(stringResource(id = R.string.sign_in))
        }
    }
}