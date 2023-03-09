package com.github.geohunt.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.github.geohunt.app.authentication.FirebaseAuthenticator
import com.github.geohunt.app.ui.theme.GeoHuntTheme
import com.github.geohunt.app.ui.theme.md_theme_light_primary
import com.github.geohunt.app.ui.theme.seed
import com.google.firebase.FirebaseApp

class StartActivity : ComponentActivity() {
    @OptIn(ExperimentalTextApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            GeoHuntTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "GeoHunt",
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                brush = Brush.linearGradient(
                                    colors = listOf(md_theme_light_primary, seed)
                                )
                            )
                        )
                    }
                }
            }
        }


    }

    override fun onStart() {
        super.onStart()

        FirebaseApp.initializeApp(this)
        val intent = if (FirebaseAuthenticator().user == null) Intent(this, MainActivity::class.java)
                    else Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}