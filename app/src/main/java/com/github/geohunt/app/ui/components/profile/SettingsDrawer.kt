package com.github.geohunt.app.ui.components.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.github.geohunt.app.R
import com.github.geohunt.app.ui.components.profile.button.FlatLongButton


@Composable
fun SettingsDrawer(
    openProfileEdit: OptionalCallback,
    openLeaderboard: OptionalCallback,
    onLogout: OptionalCallback,
    openSettings: OptionalCallback,
    close: () -> Unit
) {
    var isSureToLogOff by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .testTag("settings-drawer")) {
        openProfileEdit?.let { openProfileEdit ->
            FlatLongButton(
                icon = Icons.Default.Edit,
                text = stringResource(id = R.string.edit_profile),
                onClick = { close(); openProfileEdit() },
                modifier = Modifier.testTag("btn-open-profile-edit")
            )
        }

        openLeaderboard?.let { openLeaderboard ->
            FlatLongButton(
                icon = Icons.Default.Leaderboard,
                text = stringResource(R.string.leaderboard),
                onClick = { close(); openLeaderboard(); },
                modifier = Modifier.testTag("btn-open-leaderboard")
            )
        }

        openSettings?.let { openSettings ->
            FlatLongButton(
                icon = Icons.Default.Settings,
                text = stringResource(id = R.string.settings),
                onClick = { close(); openSettings() },
                modifier = Modifier.testTag("btn-open-settings")
            )
        }

        onLogout?.let { onLogout ->
            FlatLongButton(
                icon = Icons.Default.Logout,
                text = stringResource(if (isSureToLogOff) R.string.log_out_confirmation else R.string.log_out),
                textColor = MaterialTheme.colors.error,
                onClick = {
                    if (isSureToLogOff) { close(); onLogout() } else isSureToLogOff = true
                },
                modifier = Modifier.testTag("btn-log-off")
            )
        }
    }
}