package com.github.geohunt.app.ui.components.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun SettingsItem(
    text: String,
    content: @Composable () () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null
) {
    Row(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(text)
            description?.let {
                Text(it)
            }
        }
        Spacer(Modifier.weight(1f))
        content()
    }
}

@Composable
fun SubmenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Any,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    TextButton(modifier = modifier.fillMaxWidth(), onClick = { onClick() }) {
        Row(modifier = modifier) {
            Icon(icon, contentDescription = title)
            Column {
                Text(title)
                subtitle?.let { subtitle -> Text(subtitle) }
            }
            Icon(Icons.Default.ArrowRight, contentDescription = null)
        }
    }
}