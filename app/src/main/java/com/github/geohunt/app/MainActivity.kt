package com.github.geohunt.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.github.geohunt.app.model.database.Database
import com.github.geohunt.app.ui.MainViewModel
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
fun MainComposable(database: Database, viewModel: MainViewModel = viewModel()) {
    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState()

    val isConnected = viewModel.isConnected.collectAsStateWithLifecycle()

    LaunchedEffect(isConnected.value) {
        if (!isConnected.value) {
            scaffoldState.snackbarHostState.showSnackbar(
                message = "Could not connect to the Internet.",
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        bottomBar = {
            Surface(modifier = Modifier.shadow(Dp(9f))) {
                NavigationBar(navController = navController)
            }
        },
        scaffoldState = scaffoldState
    ) { padding ->
        NavigationController(navController = navController,
            database = database,
            Modifier.padding(padding))
    }
}