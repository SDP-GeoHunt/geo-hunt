package com.github.geohunt.app.ui.components.appbar

import androidx.compose.foundation.Image
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import com.github.geohunt.app.R

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
             IconButton(onClick = openDrawer, modifier = Modifier.testTag("mainAppBar-nav")) {
                 Icon(Icons.Default.Menu, null)
             }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun GeoHuntTitle() {
    Image(
        painterResource(R.drawable.logo),
        contentDescription = "GeoHunt"
    )
}
