package com.github.geohunt.app.ui.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.R
import com.github.geohunt.app.ui.components.navigation.Route
import com.github.geohunt.app.ui.components.navigation.TopBarWithBackButton
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.github.geohunt.app.ui.components.navigation.HiddenRoute

@Composable
fun SettingsPage(onBack: () -> Any, navigate: (Route) -> Any) {
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
            Column {

                SettingsMenuLinkQuick(stringResource(id = R.string.app_settings), Icons.Default.Settings)
                    { navigate(HiddenRoute.AppSettings) }

                SettingsMenuLinkQuick(stringResource(id = R.string.privacy_settings), Icons.Default.Shield)
                    { navigate(HiddenRoute.PrivacySettings) }
            }
        }
    }

}

@Composable
private fun SettingsMenuLinkQuick(title: String, icon: ImageVector, onClick: () -> Any) {
    SettingsMenuLink(
        title = { Text(title) },
        icon = { Icon(imageVector = icon, contentDescription = title)}
    ) { onClick() }
}