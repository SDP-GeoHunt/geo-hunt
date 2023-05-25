package com.github.geohunt.app.ui.components.appbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppBar(
    title: String,
    openDrawer: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
             IconButton(onClick = openDrawer) {
                 Icon(Icons.Default.Menu, null)
             }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun GeoHuntTitle() {
    // TODO Replace with Marwan's composable
    Text("GeoHunt")
}
