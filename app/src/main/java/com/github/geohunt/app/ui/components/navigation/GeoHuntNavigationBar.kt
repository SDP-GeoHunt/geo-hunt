package com.github.geohunt.app.ui.components.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

/**
 * Creates the bottom navigation bar of the application.
 *
 * @param navController The navigation controller.
 * @param onCreate Callback used by the create button.
 * @param visible Whether the navigation bar is visible. This should be used to hide the navigation
 *                bar on secondary screens.
 */
@Composable
fun GeoHuntNavigationBar(
    navController: NavController,
    onCreate: () -> Unit,
    visible: Boolean = true
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it })
    ) {
        NavigationBar {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination

            PrimaryScreen.values().forEach { screen ->
                NavigationBarItem(
                    selected = currentRoute?.hierarchy?.any { it.route == screen.route } == true,
                    onClick = {
                        if (screen == PrimaryScreen.Create) {
                            onCreate()
                        } else {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = screen.icon,
                    label = if (screen.label != null) {{ Text(screen.label) }} else null,
                    alwaysShowLabel = false,
                    modifier = Modifier.testTag("navBarItem-" + screen.label)
                )
            }
        }
    }
}