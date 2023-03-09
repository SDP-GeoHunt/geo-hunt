package com.github.geohunt.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.authentication.FirebaseAuthenticator
import com.github.geohunt.app.ui.theme.GeoHuntTheme
import com.github.geohunt.app.ui.theme.md_theme_light_primary
import com.github.geohunt.app.ui.theme.seed

class LoginActivity : ComponentActivity() {
    @OptIn(ExperimentalTextApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.hasExtra("login")) {
            FirebaseAuthenticator().authenticate(this@LoginActivity).thenAccept {
                it?.let {
                    startActivity(
                        Intent(this@LoginActivity, MainActivity::class.java)
                    )
                }
            }
        }

        setContent {
            GeoHuntTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "GeoHunt",
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                brush = Brush.linearGradient(
                                    colors = listOf(md_theme_light_primary, seed)
                                )
                            )
                        )

                        Button(onClick = {
                            val intent = Intent(this@LoginActivity, LoginActivity::class.java)
                            intent.putExtra("login", 1)
                            startActivity(intent)
                        }) {
                            Text("Sign in")
                        }

                    }
                }
            }
        }
    }
}