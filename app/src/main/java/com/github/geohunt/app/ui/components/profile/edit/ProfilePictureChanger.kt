package com.github.geohunt.app.ui.components.profile.edit

import android.graphics.Bitmap
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.R
import com.github.geohunt.app.model.database.api.EditedUser
import com.github.geohunt.app.ui.components.user.ProfileIcon

typealias ProfilePictureProviderType = @Composable () (((Bitmap) -> Unit)) -> (() -> Unit)

/**
 * A profile picture changer.
 *
 * @param user The user. The component will load the profile picture of the
 * @param editedUser The mutable edited user, on which every modification will be applied.
 * @param profilePictureProvider A provider for the profile picture. It is a composable that takes
 * a callback for the profile picture and returns a function that can be called to trigger the
 * "pick" of the image. By default, specify {@see profilePictureProvider}
 */
@Composable
fun ProfilePictureChanger(
    user: User,
    editedUser: MutableState<EditedUser>,
    profilePictureProvider: ProfilePictureProviderType
) {
    // Image picker
    val imagePick = profilePictureProvider {
        editedUser.value = editedUser.value.setProfilePicture(it)
    }


    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        val chosenPicture = editedUser.value.profilePicture

        if (chosenPicture == null) {
            // The user did not chose its profile picture, so we shows the default
            ProfileIcon(user, modifier = Modifier.size(128.dp))
        } else {
            // The user chose a profile picture, so we show it to him
            ProfileIcon(chosenPicture, user.name, modifier = Modifier.size(128.dp))
        }

        IconButton(
            onClick = { imagePick() },
            modifier = Modifier.testTag("edit-pick-image")
        ) {
            Icon(Icons.Default.Edit, contentDescription = stringResource(id = R.string.edit))
        }
    }
}

