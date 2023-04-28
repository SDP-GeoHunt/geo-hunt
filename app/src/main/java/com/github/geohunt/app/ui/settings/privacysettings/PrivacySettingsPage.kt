package com.github.geohunt.app.ui.settings.privacysettings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alorma.compose.settings.ui.SettingsGroup
import com.github.geohunt.app.R
import com.github.geohunt.app.ui.components.navigation.TopBarWithBackButton

@Composable
fun PrivacySettingsPage(onBack: () -> Any, viewModel: PrivacySettingsViewModel) {
    Scaffold(
        topBar = {
            TopBarWithBackButton(
                onBack = { onBack() },
                title = stringResource(id = R.string.privacy_settings)
            )
        }
    ) { pad ->
        Box(
            Modifier
                .padding(pad)
                .padding(16.dp)) {
            
            SettingsGroup {
                ProfileVisibilityChooser(viewModel = viewModel)
            }
        }
    }
}