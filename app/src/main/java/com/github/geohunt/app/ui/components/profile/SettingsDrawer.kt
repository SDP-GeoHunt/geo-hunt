package com.github.geohunt.app.ui.components.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Logout
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.github.geohunt.app.R
import com.github.geohunt.app.ui.components.button.FlatLongButton


@Composable
fun SettingsDrawer(
    openProfileEdit: OptionalCallback,
    openLeaderboard: OptionalCallback,
    onLogout: OptionalCallback,
    close: () -> Any
) {
    var isSureToLogOff by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (openProfileEdit != null) {
            FlatLongButton(
                icon = Icons.Default.Edit,
                text = stringResource(id = R.string.edit_profile),
                onClick = { close(); openProfileEdit() },
                modifier = Modifier.testTag("btn-open-profile-edit")
            )
        }

        if (openLeaderboard != null) {
            FlatLongButton(
                icon = Icons.Default.Leaderboard,
                text = stringResource(R.string.leaderboard),
                onClick = { close(); openLeaderboard(); },
                modifier = Modifier.testTag("btn-open-leaderboard")
            )
        }

        if (onLogout != null) {
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
