package com.github.geohunt.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.github.geohunt.app.ui.components.tutorial.Tutorial
import com.github.geohunt.app.ui.components.tutorial.WelcomeScreen
import com.github.geohunt.app.ui.theme.GeoHuntTheme
import com.github.geohunt.app.utility.replaceActivity
import kotlin.math.roundToInt

/**
 * The activity that handles display of the tutorial
 */
class TutorialActivity : ComponentActivity()  {
    private fun goToLogin(activity: ComponentActivity) {
        activity.replaceActivity(Intent(activity, LoginActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GeoHuntTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val activity = this@TutorialActivity
                    val shouldShowTutorial = remember { mutableStateOf(false) }

                    val configuration = LocalConfiguration.current
                    val screenDensity = configuration.densityDpi / 160f
                    val screenHeightPx =
                        (configuration.screenHeightDp.toFloat() * screenDensity).roundToInt()
                    val screenWidthPx =
                        (configuration.screenWidthDp.toFloat() * screenDensity).roundToInt()

                    // If the screen is too small, skip displaying the welcome screen and tutorial
                    if (screenHeightPx < 800 || screenWidthPx < 480) {
                        goToLogin(activity)
                    }

                    if (shouldShowTutorial.value) {
                        Tutorial { goToLogin(activity) }
                    }
                    else {
                        WelcomeScreen { shouldShowTutorial.value = true }
                    }
                }
            }
        }
    }
}
