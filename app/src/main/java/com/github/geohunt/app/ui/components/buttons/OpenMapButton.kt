package com.github.geohunt.app.ui.components.buttons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable

@Composable
fun OpenMapButton(
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            Icons.Outlined.Map,
            contentDescription = "Open the map"
        )
    }
}