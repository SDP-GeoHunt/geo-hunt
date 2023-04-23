package com.github.geohunt.app.ui.components.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.R
import com.github.geohunt.app.settings.SettingsStore
import com.github.geohunt.app.ui.components.navigation.TopBarWithBackButton
import com.github.geohunt.app.ui.components.settings.items.ThemeSettingItem

@Composable
fun AppSettingsPage(onBack: () -> Any) {
    val settingsStore = SettingsStore.get()

    Scaffold(
        topBar = {
            TopBarWithBackButton(
                onBack = { onBack() },
                title = stringResource(id = R.string.app_settings)
            )
        }
    ) { pad ->
        Box(
            Modifier
                .padding(pad)
                .padding(16.dp)) {
            Row {
                ThemeSettingItem(settingsStore)
            }
        }
    }
}