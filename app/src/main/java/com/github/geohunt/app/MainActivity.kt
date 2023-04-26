package com.github.geohunt.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.github.geohunt.app.model.database.Database
import com.github.geohunt.app.ui.screens.main.MainScreen
import com.github.geohunt.app.ui.screens.main.MainViewModel
import com.github.geohunt.app.ui.theme.GeoHuntTheme

/**
 * Main activity launched on application start.
 *
 * The activity will check that the user is properly logged in before accessing the app.
 * If the user is not logged, he is redirected to [LoginActivity].
 */
class MainActivity : ComponentActivity() {

    private lateinit var database : Database

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ask for login if the user is not logged in
        if (!viewModel.auth.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        database = Database.createDatabaseHandle(this)

        setContent {
            GeoHuntTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen(database, viewModel)
                }
            }
        }
    }
}
