package com.github.geohunt.app.ui.settings.privacysettings

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import com.alorma.compose.settings.ui.SettingsGroup
import com.github.geohunt.app.R
import com.github.geohunt.app.model.database.api.ProfileVisibility
import com.github.geohunt.app.ui.settings.RadioItem

@Composable
fun ProfileVisibilityChooser(viewModel: PrivacySettingsViewModel) {
    val visibility = viewModel.profileVisibility.collectAsState()
    val callback: (ProfileVisibility) -> Unit = { viewModel.setProfileVisibility(it) }

    SettingsGroup(title = { Text(stringResource(id = R.string.profile_visibility)) }) {
        RadioItem(
            title = stringResource(id = R.string.public_word),
            subtitle = stringResource(id = R.string.public_desc),
            isSelected = visibility.value == ProfileVisibility.PUBLIC,
            value = ProfileVisibility.PUBLIC,
            onSelect = callback
        )
        RadioItem(
            title = stringResource(id = R.string.following_only),
            subtitle = stringResource(id = R.string.following_only_desc),
            isSelected = visibility.value == ProfileVisibility.FOLLOWING_ONLY,
            value = ProfileVisibility.FOLLOWING_ONLY,
            onSelect = callback
        )
        RadioItem(
            title = stringResource(id = R.string.private_word),
            subtitle = stringResource(id = R.string.private_desc),
            isSelected = visibility.value == ProfileVisibility.PRIVATE,
            value = ProfileVisibility.PRIVATE,
            onSelect = callback
        )
    }
}