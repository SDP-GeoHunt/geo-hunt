package com.github.geohunt.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.github.geohunt.app.data.repository.AppContainer
import com.github.geohunt.app.ui.screens.GeoHuntScreen
import com.github.geohunt.app.ui.screens.main.MainScreen
import com.github.geohunt.app.ui.screens.main.MainViewModel
import com.github.geohunt.app.utility.replaceActivity

/**
 * Main activity.
 *
 * The activity will check that the user is properly logged in before accessing the app.
 * If the user is not logged, he is redirected to [LoginActivity].
 */
class MainActivity : ComponentActivity() {

    private lateinit var container: AppContainer
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        container = AppContainer.getInstance(application)
        viewModel = MainViewModel(container.auth)

        setContent {
            GeoHuntScreen {
                MainScreen(viewModel, logout = {
                    viewModel.logout(this@MainActivity, then = {
                        replaceActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    })
                })
            }
        }
    }
}
