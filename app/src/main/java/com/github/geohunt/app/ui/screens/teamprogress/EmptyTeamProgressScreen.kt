package com.github.geohunt.app.ui.screens.teamprogress

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.geohunt.app.ui.theme.GeoHuntTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmptyTeamProgressScreen(
    title: String,
    onBack: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    GeoHuntTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back button"
                            )
                        }
                    }
                )
            }
        ) {
            content(it)
        }
    }
}