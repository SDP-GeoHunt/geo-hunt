package com.github.geohunt.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.github.geohunt.app.ui.components.NavigationBar
import com.github.geohunt.app.ui.components.NavigationController
import com.github.geohunt.app.ui.theme.GeoHuntTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GeoHuntTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Icon(Icons.Sharp.Home, null)
                    MainComposable()
                }
            }
        }
    }
}

@Composable
fun MainComposable() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { NavigationBar(navController = navController) }
    ) { padding ->
        NavigationController(navController = navController, Modifier.padding(padding))
    }
}