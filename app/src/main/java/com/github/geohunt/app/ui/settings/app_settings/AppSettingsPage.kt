package com.github.geohunt.app.ui.settings.app_settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.R
import com.github.geohunt.app.data.settings.Theme
import com.github.geohunt.app.ui.components.appbar.TopAppBarWithBackButton
import com.github.geohunt.app.ui.settings.SettingsListDropdownQuick

@Composable
fun AppSettingsPage(
    onBack: () -> Unit,
    viewModel: AppSettingsViewModel
) {
    val theme = viewModel.theme.collectAsState()

    Scaffold(
        topBar = {
            TopAppBarWithBackButton(
                onBack = onBack,
                title = stringResource(id = R.string.app_settings)
            )
        }
    ) { pad ->
        Box(
            Modifier
                .padding(pad)
                .padding(16.dp)) {
            Row {
                SettingsListDropdownQuick(
                    title = stringResource(R.string.theme),
                    icon = Icons.Default.DarkMode,
                    value = theme.value.ordinal,
                    items = Theme.values().map {
                        when(it) {
                            Theme.DARK -> stringResource(id = R.string.dark)
                            Theme.LIGHT -> stringResource(id = R.string.light)
                            Theme.SYSTEM -> stringResource(id = R.string.system)
                        }
                    },
                    onItemSelected = { index, _ -> viewModel.setTheme(Theme.values()[index]) },
                    modifier = Modifier.testTag("settings-theme")
                )
            }
        }
    }
}
