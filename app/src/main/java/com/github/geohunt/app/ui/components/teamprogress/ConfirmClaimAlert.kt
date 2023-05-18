package com.github.geohunt.app.ui.components.teamprogress

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.github.geohunt.app.R
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
                    Text(stringResource(id = R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = onCancel) {
                    Text(stringResource(id = R.string.cancel))
                }
            },
            title = { Text(stringResource(id = R.string.claim_confirmation_alert_title)) },
            text = {
                Text(stringResource(id = R.string.claim_confirmation_alert_text))
            }
        )
    }

}