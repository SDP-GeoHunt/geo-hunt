package com.github.geohunt.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.github.geohunt.app.ui.theme.GeoHuntTheme
import com.github.geohunt.app.ui.components.tutorial.Tutorial
import com.github.geohunt.app.ui.components.tutorial.WelcomeScreen

class TutorialActivity : ComponentActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GeoHuntTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                )
                {
                    val activity = this@TutorialActivity
                    val shouldShowTutorial = remember { mutableStateOf(false) }

                    if (shouldShowTutorial.value) {
                        Tutorial(activity)
                    }
                    else {
                        WelcomeScreen(shouldShowTutorial)
                    }
                }
            }
        }
    }
}
