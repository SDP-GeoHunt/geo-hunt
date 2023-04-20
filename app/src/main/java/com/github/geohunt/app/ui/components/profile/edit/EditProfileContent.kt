package com.github.geohunt.app.ui.components.profile.edit

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.github.geohunt.app.R
import com.github.geohunt.app.model.database.api.EditedUser
import com.github.geohunt.app.model.database.api.LoggedUserContext
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.ui.components.button.FlatLongButton

@Composable
fun LoggedUserContext.EditProfileContent(user: User) {
    instrumentableEditProfileContent(user = user)
}

@Composable
@NoLiveLiterals
fun LoggedUserContext.instrumentableEditProfileContent(user: User): MutableState<EditedUser> {
    val editedUser = remember { mutableStateOf(EditedUser(user.name)) }
    var isSaving by remember { mutableStateOf(false) }
    val context = LocalContext.current

    fun save() {
        isSaving = true
        val newUser = editedUser.value
        updateLoggedUser(newUser)
            .addOnCompleteListener { isSaving = false }
            .addOnFailureListener {
                Toast.makeText(context, "Error while updating profile.", Toast.LENGTH_LONG).show()
                isSaving = false
            }
    }

    Column {
        ProfilePictureChanger(user, editedUser) { profilePictureProvider(it) }

        DisplayNameChanger(editedUser)

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
    
    return editedUser
}