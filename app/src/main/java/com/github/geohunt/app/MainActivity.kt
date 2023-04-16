package com.github.geohunt.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.Dp
import androidx.navigation.compose.rememberNavController
import com.github.geohunt.app.authentication.Authenticator
import com.github.geohunt.app.model.database.api.Database
import com.github.geohunt.app.ui.components.navigation.NavigationBar
import com.github.geohunt.app.ui.components.navigation.NavigationController
import com.github.geohunt.app.ui.theme.GeoHuntTheme

class MainActivity : ComponentActivity() {

    private lateinit var database : Database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Database.createDatabaseHandle(this)

        setContent {
            GeoHuntTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainComposable(database)
                }
            }
        }
    }
}

@Composable
fun MainComposable(database: Database) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            Surface(modifier = Modifier.shadow(Dp(9f))) {
                NavigationBar(navController = navController)
            }
        }
    ) { padding ->
        NavigationController(navController = navController,
            database = database,
            Modifier.padding(padding))
    }
}