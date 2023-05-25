package com.github.geohunt.app.ui.components.appbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.R
import kotlinx.coroutines.launch

enum class HomeScreenFeed(
    val title: @Composable () -> String,
    val icon: @Composable () -> Unit
) {
    Home(
        title = { stringResource(id = R.string.home) },
        icon = { Icon(Icons.Default.Home, null) }
    ),
    Discover(
        title = { stringResource(id = R.string.discover) },
        icon = { Icon(Icons.Default.Public, null) }
    ),
    Bounties(
        title = { stringResource(id = R.string.bounties) },
        icon = { Icon(Icons.Default.ListAlt, null) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedSelectionDrawer(
    content: @Composable (feed: HomeScreenFeed, openDrawer: () -> Unit) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val currentFeed = remember { mutableStateOf(HomeScreenFeed.Home) }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Box(Modifier.padding(12.dp)) {
                    GeoHuntTitle()
                }

                HomeScreenFeed.values().forEach { feed ->
                    NavigationDrawerItem(
                        icon = feed.icon,
                        label = { Text(feed.title()) },
                        selected = feed == currentFeed.value,
                        onClick = {
                            scope.launch { drawerState.close() }
                            currentFeed.value = feed
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
        content = {
            content(feed = currentFeed.value, openDrawer = { scope.launch { drawerState.open() }})
        }
    )
}