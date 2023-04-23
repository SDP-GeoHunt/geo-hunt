package com.github.geohunt.app.ui.components.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.R
import com.github.geohunt.app.settings.SettingsStore
import com.github.geohunt.app.ui.components.navigation.HiddenRoute
import com.github.geohunt.app.ui.components.navigation.Route
import com.github.geohunt.app.ui.components.navigation.VisibleRoute
import com.github.geohunt.app.ui.components.navigation.TopBarWithBackButton

@Composable
fun SettingsPage(navigate: (Route) -> Any, onBack: () -> Any) {
    Scaffold(
        topBar = {
            TopBarWithBackButton(
                onBack = { onBack() },
                title = stringResource(id = R.string.settings)
            )
        }
    ) { pad ->
        Box(
            Modifier
                .padding(pad)
                .padding(16.dp)) {
            Row {
                SubmenuItem(
                    icon = Icons.Default.Settings,
                    title = stringResource(id = R.string.app_settings),
                    onClick = { navigate(HiddenRoute.AppSettings) })

                SubmenuItem(
                    icon = Icons.Default.Shield,
                    title = stringResource(id = R.string.privacy_settings),
                    onClick = { navigate(HiddenRoute.PrivacySettings) })
            }
        }
    }

}