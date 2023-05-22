@file:OptIn(ExperimentalMaterialApi::class)

package com.github.geohunt.app.ui.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.github.geohunt.app.ui.components.CreateMenuPopup
import com.github.geohunt.app.ui.components.navigation.SecondaryScreen
import com.github.geohunt.app.ui.components.navigation.GeoHuntNavigationBar
import com.github.geohunt.app.ui.components.navigation.NavigationController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen(viewModel: MainViewModel, logout: () -> Unit) {
    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState()
    val modalState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    val isConnected = viewModel.isConnected.collectAsStateWithLifecycle()

    if (!isConnected.value) {
        LaunchedEffect(scaffoldState.snackbarHostState) {
            scaffoldState.snackbarHostState.showSnackbar(
                message = "Could not connect to the Internet.",
                duration = SnackbarDuration.Short
            )
        }
    }

    CreateMenuPopup(
        modalState,
        createChallenge = { navController.navigate(SecondaryScreen.CreateChallenge.route) },
        createBounty = { navController.navigate(SecondaryScreen.CreateBounty.route) },
        scope
    ) {
        Scaffold(
            bottomBar = {
                Surface(modifier = Modifier.shadow(9.dp)) {
                    GeoHuntNavigationBar(navController = navController, onCreate = {
                        scope.launch { modalState.show() }
                    })
                }
            },
            scaffoldState = scaffoldState
        ) { padding ->

                NavigationController(
                    navController = navController,
                    modifier = Modifier.padding(padding),
                    logout = logout
                )
        }
    }
}