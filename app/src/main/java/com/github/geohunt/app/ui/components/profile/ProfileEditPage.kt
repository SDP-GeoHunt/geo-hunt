package com.github.geohunt.app.ui.components.profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.github.geohunt.app.R
import com.github.geohunt.app.authentication.Authenticator
import com.github.geohunt.app.model.database.Database
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.ui.components.button.FlatLongButton
import com.github.geohunt.app.ui.components.navigation.TopBarWithBackButton
import com.github.geohunt.app.ui.rememberLazyRef
import com.github.geohunt.app.utility.findActivity
import com.github.geohunt.app.utility.thenMap

data class EditedUser(
    var displayName: String,
    var profilePicture: Bitmap?,
    var isProfilePictureNew: Boolean = false
) {
    /**
     * Apply all updates except profile picture
     */
    fun applyUpdates(user: User): User {
        user.displayName = this.displayName
        return user
    }

    companion object {
        fun fromUser(user: User): EditedUser {
            return EditedUser(user.name, null)
        }
    }
}

@Composable
fun ProfileEditPage(onBackButton: () -> Any) {
    // Getting user
    val uid = Authenticator.authInstance.get().user!!.uid
    val act = LocalContext.current.findActivity()
    val user = rememberLazyRef {
        Database.databaseFactory.get()(act).getUser(uid)
    }

    Scaffold(
        topBar = {
            TopBarWithBackButton(
                onBack = { onBackButton() },
                title = stringResource(id = R.string.edit_profile)
            )
        }
    ) { pad ->
        Box(
            Modifier
                .padding(pad)
                .padding(16.dp)) {
            if (user.value == null) {
                Text("Loading") // TODO: replace
            } else {
                SettingsPageContent(user.value!!)
            }
        }
    }
}

@Composable
private fun SettingsPageContent(user: User) {
    val editedUser = remember { mutableStateOf(EditedUser.fromUser(user)) }
    val db = Database.databaseFactory.get()(LocalContext.current.findActivity())
    var isSaving by remember { mutableStateOf(false) }

    fun save() {
        isSaving = true
        val newUser = editedUser.value
        db.updateUser(newUser.applyUpdates(user), if (newUser.isProfilePictureNew) newUser.profilePicture else null)
            .thenMap {
                isSaving = false
            }
    }

    Column {
        ProfilePictureChanger(user, editedUser)

        DisplayNameChanger(user, editedUser)

        if (isSaving) {
            CircularProgressIndicator()
        } else {
            FlatLongButton(
                icon = Icons.Default.Save,
                text = stringResource(R.string.save),
                onClick = { save() })
        }
    }
}

@Composable
private fun DisplayNameChanger(user: User, editedUser: MutableState<EditedUser>) {
    Row {
       TextField(
           modifier = Modifier.fillMaxWidth(),
           value = editedUser.value.displayName,
           onValueChange = { editedUser.value = editedUser.value.copy(displayName = it) },
           label = { Text(stringResource(id = R.string.display_name)) },
           singleLine = true
       )
    }
}

@Composable
private fun ProfilePictureChanger(user: User, editedUser: MutableState<EditedUser>) {
    val currentProfilePicture = rememberLazyRef { user.profilePicture }

    // Load current profile picture
    currentProfilePicture.value?.let {
        println("fetched profile picture")
        editedUser.value = editedUser.value.copy(profilePicture = it)
    }

    val ctx = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { it?.let {
            editedUser.value = editedUser.value.copy(
                profilePicture = uriToBitMap(ctx, it),
                isProfilePictureNew = true
            )
        } } )

    fun launchImagePicker() {
        imagePickerLauncher.launch(PickVisualMediaRequest(
            mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
        ))
    }

    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
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
        IconButton(onClick = { launchImagePicker() }) {
            Icon(Icons.Default.Edit, contentDescription = stringResource(id = R.string.edit))
        }
    }
}
private fun uriToBitMap(ctx: Context, uri: Uri): Bitmap {
    val source = ImageDecoder.createSource(ctx.contentResolver, uri)

    return ImageDecoder.decodeBitmap(source)
}