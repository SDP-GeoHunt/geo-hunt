package com.github.geohunt.app.ui.components.profile.edit

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.R
import com.github.geohunt.app.ui.components.user.DefaultProfileIcon
import com.github.geohunt.app.ui.components.user.ProfileIcon

typealias ProfilePictureProviderType = @Composable () (((Uri) -> Unit)) -> (() -> Unit)

/**
 * A profile picture changer.
 *
 * @param currentImage The current image to display as profile picture. If null, will show default profile picture.
 * @param onImageSelected The callback that will be triggered on every selection of a new profile picture.
 * @param profilePictureProvider The provider of the image picker.
 */
@Composable
fun ProfilePictureChanger(
    currentImage: Any?,
    onImageSelected: (Uri) -> Any,
    profilePictureProvider: ProfilePictureProviderType
) {
    // Image picker
    val imagePick = profilePictureProvider {
        onImageSelected(it)
    }


    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        if (currentImage == null) {
            // The user did not chose its profile picture, so we shows the default
            DefaultProfileIcon(modifier = Modifier.size(128.dp))
        } else {
            // The user chose a profile picture, so we show it to him
            ProfileIcon(currentImage, "", modifier = Modifier.size(128.dp))
        }

        IconButton(
            onClick = { imagePick() },
            modifier = Modifier.testTag("edit-pick-image")
        ) {
            Icon(Icons.Default.Edit, contentDescription = stringResource(id = R.string.edit))
        }
    }
}

