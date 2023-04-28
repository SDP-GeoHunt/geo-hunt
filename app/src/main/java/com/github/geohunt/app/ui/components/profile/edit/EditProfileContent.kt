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
import com.github.geohunt.app.model.database.Database
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.ui.components.button.FlatLongButton
import com.github.geohunt.app.utility.findActivity
import com.github.geohunt.app.R
import com.github.geohunt.app.model.database.api.EditedUser

@Composable
fun EditProfileContent(database: Database, user: User) {
    instrumentableEditProfileContent(database, user = user)
}

@Composable
@NoLiveLiterals
fun instrumentableEditProfileContent(db: Database, user: User): MutableState<EditedUser> {
    val editedUser = remember { mutableStateOf(EditedUser.fromUser(user)) }
    var isSaving by remember { mutableStateOf(false) }
    val ctx = LocalContext.current

    fun save() {
        isSaving = true
        val newUser = editedUser.value
        db.updateUser(newUser)
            .addOnCompleteListener { isSaving = false }
            .addOnFailureListener {
                Toast.makeText(ctx, "Error while updating profile.", Toast.LENGTH_LONG).show()
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