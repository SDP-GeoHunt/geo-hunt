package com.github.geohunt.app.ui.components.profile.edit

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.github.geohunt.app.R
import com.github.geohunt.app.model.EditedUser
import com.github.geohunt.app.model.User
import com.github.geohunt.app.ui.components.profile.button.FlatLongButton

@Composable
fun EditProfileContent(
    user: User,
    eu: EditedUser,
    onDisplayNameChange: (String) -> Any,
    onProfilePictureChange: (Uri) -> Any,
    isSaving: Boolean,
    save: () -> Any
) {
    Column {
        ProfilePictureChanger(eu.newProfilePicture?.uri ?: user.profilePictureUrl, { onProfilePictureChange(it) }) { profilePictureProvider(it) }
        DisplayNameChanger(eu.newDisplayName ?: "") { onDisplayNameChange(it) }

        if (isSaving) {
            FlatLongButton(
                icon = Icons.Default.Pending,
                modifier = Modifier.testTag("wait-btn"),
                text = stringResource(id = R.string.saving), onClick = { })
        } else {
            FlatLongButton(
                icon = Icons.Default.Save,
                modifier = Modifier.testTag("save-btn"),
                text = stringResource(R.string.save),
                onClick = { save() })
        }
    }
}