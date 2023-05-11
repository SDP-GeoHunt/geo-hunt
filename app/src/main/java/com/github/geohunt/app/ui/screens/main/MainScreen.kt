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
import com.github.geohunt.app.ui.components.navigation.NavigationBar
import com.github.geohunt.app.ui.components.navigation.NavigationController
import kotlinx.coroutines.launch

@Composable
fun MainScreen(viewModel: MainViewModel, logout: () -> Any) {
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

    CreateMenuPopup(modalState, navController, scope) {
        Scaffold(
            bottomBar = {
                Surface(modifier = Modifier.shadow(9.dp)) {
                    NavigationBar(navController = navController) { // on create
                        scope.launch { modalState.show() }
                    }
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