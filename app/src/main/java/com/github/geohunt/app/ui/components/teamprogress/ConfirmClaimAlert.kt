package com.github.geohunt.app.ui.components.teamprogress

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import com.github.geohunt.app.ui.theme.geoHuntRed

@Composable
fun ConfirmClaimAlert(
    showAlert: Boolean,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    if (showAlert) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                TextButton(onClick = onConfirm, colors = ButtonDefaults.textButtonColors(contentColor = geoHuntRed)) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = onCancel) {
                    Text("Cancel")
                }
            },
            title = { Text("Confirm claim") },
            text = {
                Text("Are you sure you want to claim this challenge ?")
            }
        )
    }

}