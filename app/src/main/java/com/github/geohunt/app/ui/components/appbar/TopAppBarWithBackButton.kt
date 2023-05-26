package com.github.geohunt.app.ui.components.appbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import com.github.geohunt.app.ui.components.buttons.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarWithBackButton(onBack: () -> Unit, title: String, actions: @Composable RowScope.() -> Unit = {}) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = { BackButton(onClick = onBack) },
        actions = actions
    )
}