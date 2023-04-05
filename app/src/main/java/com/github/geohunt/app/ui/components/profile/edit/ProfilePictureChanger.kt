package com.github.geohunt.app.ui.components.profile.edit

import android.graphics.Bitmap
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.ui.rememberLazyRef
import com.github.geohunt.app.R

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
    val currentProfilePicture = rememberLazyRef { user.profilePicture }

    // Load current profile picture
    currentProfilePicture.value?.let {
        if (!editedUser.value.isProfilePictureNew)
        // is there an issue with races?
            editedUser.value = editedUser.value.copy(profilePicture = it)
    }


    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        val imagePick = profilePictureProvider {
            editedUser.value = editedUser.value.setProfilePicture(it)
        }

        // TODO: change
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(editedUser.value.profilePicture)
                .crossfade(true)
                .build(),
            contentDescription = "${user.name} profile picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(124.dp)
                .aspectRatio(1f)
                .padding(8.dp)
                .clip(CircleShape)
        )
        IconButton(
            onClick = { imagePick() },
            modifier = Modifier.testTag("edit-pick-image")
        ) {
            Icon(Icons.Default.Edit, contentDescription = stringResource(id = R.string.edit))
        }
    }
}

