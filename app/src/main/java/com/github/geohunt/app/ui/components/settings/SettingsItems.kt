package com.github.geohunt.app.ui.components.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun DropdownSettingsItem(
    text: String,
    content: @Composable () () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null
) {
    Row(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(text, style = MaterialTheme.typography.body1)
            description?.let {
                Text(it, style = MaterialTheme.typography.subtitle2)
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
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(title)
                subtitle?.let { subtitle -> Text(subtitle) }
            }
            Spacer(Modifier.weight(1f))
            Icon(Icons.Default.ArrowRight, contentDescription = null)
        }
    }
}

@Composable
fun <T> RadioItem (
    text: String,
    subtext: String?,
    state: MutableState<T>,
    value: T
) {
    Row(
        Modifier
            .fillMaxWidth()
            .selectable(
                selected = state.value == value,
                onClick = { state.value = value }
            )
    ) {
        RadioButton(selected = state.value == value, onClick = { state.value = value })

        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text = text,
                style = MaterialTheme.typography.body1.merge()
            )
            subtext?.let {
                Text(text = it, style = MaterialTheme.typography.subtitle2.merge())
            }
        }

    }
}