package com.github.geohunt.app.ui.components.profile.edit

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.github.geohunt.app.R

@Composable
fun DisplayNameChanger(editedUser: MutableState<EditedUser>) {
    Row {
       TextField(
           modifier = Modifier.fillMaxWidth().testTag("display-name-input"),
           value = editedUser.value.displayName,
           onValueChange = { editedUser.value = editedUser.value.copy(displayName = it) },
           label = { Text(stringResource(id = R.string.display_name)) },
           singleLine = true
       )
    }
}
