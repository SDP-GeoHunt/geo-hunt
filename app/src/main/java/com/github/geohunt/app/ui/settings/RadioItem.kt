package com.github.geohunt.app.ui.settings

import androidx.compose.material.Icon
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.alorma.compose.settings.ui.SettingsMenuLink

@Composable
fun <T> RadioItem(
    title: String,
    value: T,
    isSelected: Boolean,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    subtitle: String? = null,
    disabled: Boolean = false
) {
    SettingsMenuLink(
        modifier = modifier,
        title = { Text(title) },
        subtitle = { subtitle?.let { subtitle -> Text(subtitle) } },
        icon = icon?.let { { Icon(icon, contentDescription = title) } },
        action = {
            RadioButton(selected = isSelected, onClick = { onSelect(value) }, enabled = !disabled)
        }
    ) {
        onSelect(value)
    }
}