package com.github.geohunt.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.github.geohunt.app.data.repository.AppContainer
import com.github.geohunt.app.model.database.Database
import com.github.geohunt.app.ui.screens.GeoHuntScreen
import com.github.geohunt.app.ui.screens.main.MainScreen
import com.github.geohunt.app.ui.screens.main.MainViewModel

/**
 * Main activity.
 *
 * The activity will check that the user is properly logged in before accessing the app.
 * If the user is not logged, he is redirected to [LoginActivity].
 */
class MainActivity : ComponentActivity() {
    private lateinit var database: Database

    private lateinit var container: AppContainer
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Database.createDatabaseHandle(this)
        container = AppContainer.getInstance(application)
        viewModel = MainViewModel(container.auth)

        // Ask for login if the user is not logged in
        if (!viewModel.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        setContent {
            GeoHuntScreen {
                MainScreen(database, viewModel)
            }
        }
    }
}
