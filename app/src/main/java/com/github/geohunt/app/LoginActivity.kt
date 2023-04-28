package com.github.geohunt.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.authentication.Authenticator
import com.github.geohunt.app.ui.theme.GeoHuntTheme
import com.github.geohunt.app.ui.theme.md_theme_light_primary
import com.github.geohunt.app.ui.theme.seed
import com.github.geohunt.app.utility.replaceActivity

class LoginActivity : ComponentActivity() {
    val PREFERENCES_FILE = "Preferences"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authenticator: Authenticator = Authenticator.authInstance.get()
        val settings = getSharedPreferences(PREFERENCES_FILE, 0)

        //settings.edit().putBoolean("first_application_open", true).commit()

        // Check if the application is being launched for first time
        // and if so, display the tutorial
        if (settings.getBoolean("first_application_open", true)) {
            val intent = Intent(this@LoginActivity, TutorialActivity::class.java)
            replaceActivity(intent)
            settings.edit().putBoolean("first_application_open", false).commit()
        }

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