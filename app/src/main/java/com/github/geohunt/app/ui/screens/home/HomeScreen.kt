package com.github.geohunt.app.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.geohunt.app.R
import com.github.geohunt.app.ui.components.utils.ShowException

internal enum class HomeScreens(val title: @Composable () -> String) {
    Feed({ stringResource(id = R.string.feed) }),
    Bounties({ stringResource(id = R.string.bounties) })
}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
    navigate: (String) -> Unit
) {
    var currentScreen by remember { mutableStateOf(HomeScreens.Feed) }
    val didFail by viewModel.initFailException.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        if (didFail != null) {
            ShowException(e = didFail!!)
        } else {
            Surface(elevation = 8.dp) {
                HomeScreenSelector(currentScreen = currentScreen, onChange = { currentScreen = it })
            }

            Spacer(Modifier.height(16.dp))

            when(currentScreen) {
                HomeScreens.Feed -> HomeFeed(viewModel = viewModel)
                HomeScreens.Bounties -> HomeBounties(vm = viewModel, navigate)
            }
        }
    }
}

