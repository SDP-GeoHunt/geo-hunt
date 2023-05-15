package com.github.geohunt.app.ui.components.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.github.geohunt.app.R

@Composable
fun TopBarWithBackButton(onBack: () -> Any, title: String, rightContent: @Composable () () -> Unit = {}) {
    TopAppBar {
        IconButton(
            onClick = { onBack() },
            modifier = Modifier.testTag("back-btn")
        ) {
            Icon(Icons.Default.ArrowBack, stringResource(id = R.string.navigation_back))
        }

        Text(title)

        Spacer(Modifier.weight(1f))

        rightContent()
    }
}