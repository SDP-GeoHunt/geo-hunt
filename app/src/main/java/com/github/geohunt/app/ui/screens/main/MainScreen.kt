package com.github.geohunt.app.ui.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Surface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.github.geohunt.app.model.database.Database
import com.github.geohunt.app.ui.components.navigation.NavigationBar
import com.github.geohunt.app.ui.components.navigation.NavigationController

@Composable
fun MainScreen(database: Database, viewModel: MainViewModel) {
    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState()

    val isConnected = viewModel.isConnected.collectAsStateWithLifecycle()

    if (!isConnected.value) {
        LaunchedEffect(scaffoldState.snackbarHostState) {
            scaffoldState.snackbarHostState.showSnackbar(
                message = "Could not connect to the Internet.",
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        bottomBar = {
            Surface(modifier = Modifier.shadow(9.dp)) {
                NavigationBar(navController = navController)
            }
        },
        scaffoldState = scaffoldState
    ) { padding ->
        NavigationController(
            navController = navController,
            database = database,
            modifier = Modifier.padding(padding)
        )
    }
}