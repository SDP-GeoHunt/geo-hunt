package com.github.geohunt.app.ui.screens.teamprogress

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.geohunt.app.ui.components.appbar.TopAppBarWithBackButton
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
                TopAppBarWithBackButton(
                    title = title,
                    onBack = onBack
                )
            }
        ) {
            content(it)
        }
    }
}