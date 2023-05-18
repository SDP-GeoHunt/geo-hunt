package com.github.geohunt.app.ui.components.profile.button

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * Implements a Drawer button. A button with full width, as in a table.
 *
 * Mainly inspired by https://github.com/SmartToolFactory/Jetpack-Compose-Tutorials/blob/master/Tutorial1-1Basics/src/main/java/com/smarttoolfactory/tutorial1_1basics/ui/components/DrawerButton.kt
 *
 * @param icon The icon to display
 * @param text Text
 * @param onClick On click callback
 * @param modifier Modifiers
 */
@Composable
fun FlatLongButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Any,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.typography.bodyMedium.color,
    disabled: Boolean = false
) {
    val surfaceModifier = modifier
        .padding(start = 8.dp, top = 8.dp, end = 8.dp)
        .fillMaxWidth()

    Surface(
        modifier = surfaceModifier,
        shape = MaterialTheme.shapes.small
    ) {
        TextButton(
            onClick = { onClick() },
            enabled = !disabled,
            modifier = Modifier.fillMaxWidth(),
            colors = run {
                ButtonDefaults.buttonColors(containerColor = Color.Unspecified, contentColor = textColor)
            }) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(icon, text)
                Spacer(Modifier.width(16.dp))
                Text(
                    text = text
                )
            }
        }
    }
}