package com.github.geohunt.app.ui.components.bounties

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.github.geohunt.app.model.Bounty

@Composable
internal fun RenameBountyPopup(bounty: Bounty, onDismiss: (String?) -> Unit) {
    var name by remember(bounty) {
        mutableStateOf(bounty.name)
    }

    AlertDialog(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp, 20.dp),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        title = {
            Text(text = "Renaming bounty",
                fontSize = 25.sp)
        },
        text = {
            Column {
                Spacer(modifier = Modifier.height(20.dp))

                TextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("<Name of Bounty>") },
                    modifier = Modifier.testTag("rename-field")
                )
            }
        },
        buttons = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button({ onDismiss(name.takeIf { name != bounty.name }) }) {
                    Text(text = "Ok")
                }

                Spacer(modifier = Modifier.width(15.dp))

                Button(onClick = { onDismiss(null) }) {
                    Text(text = "Cancel")
                }
            }
        },
        onDismissRequest = { onDismiss(null) }
    )
}
