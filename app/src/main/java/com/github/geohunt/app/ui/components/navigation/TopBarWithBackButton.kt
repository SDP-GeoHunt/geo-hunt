package com.github.geohunt.app.ui.components.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.geohunt.app.ui.components.buttons.BackButton

@Composable
fun TopBarWithBackButton(onBack: () -> Unit, title: String, rightContent: @Composable () -> Unit = {}) {
    TopAppBar {
        BackButton(onClick = onBack)

        Text(title)

        Spacer(Modifier.weight(1f))

        rightContent()
    }
}