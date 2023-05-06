package com.github.geohunt.app.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandCircleDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.geohunt.app.R

private enum class HomeScreens(val title: @Composable () -> String) {
    Feed({ stringResource(id = R.string.feed) }),
    Bounties({ stringResource(id = R.string.bounties) })
}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
    navigate: (String) -> Any
) {
    var currentScreen by remember { mutableStateOf(HomeScreens.Feed) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

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

@Composable
private fun HomeScreenSelector(currentScreen: HomeScreens, onChange: (HomeScreens) -> Unit) {
    var isDropMenuVisible by remember { mutableStateOf(false) }

    @Composable
    fun generateDropDownMenuItem(item: HomeScreens) {
        DropdownMenuItem(onClick = { isDropMenuVisible = false; onChange(item) }) {
            Text(item.title())
        }
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .height(46.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            TextButton(
                onClick = { isDropMenuVisible = true },
                modifier = Modifier
            ) {
                Text(currentScreen.title())
                Spacer(Modifier.width(8.dp))
                Icon(imageVector = Icons.Default.ExpandCircleDown, contentDescription = null)
            }
            Spacer(Modifier.weight(1f))
            Text(stringResource(id = R.string.app_name), color = MaterialTheme.colors.primary)
        }

        DropdownMenu(
            expanded = isDropMenuVisible,
            onDismissRequest = { isDropMenuVisible = false }
        ) {
            generateDropDownMenuItem(item = HomeScreens.Feed)
            generateDropDownMenuItem(item = HomeScreens.Bounties)
        }
    }

}