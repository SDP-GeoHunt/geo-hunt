package com.github.geohunt.app.ui.components.buttons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Creates a [DropdownMenuItem] in a [MenuButton]'s dropdown.
 *
 * @param title The title of the item.
 * @param icon A descriptive leading icon.
 * @param onClick The click handler.
 * @param enabled Whether the item is enabled.
 * @param red Whether the item should be displayed in red (only for destructive operations)
 */
@Composable
fun MenuItem(
    title: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    enabled: Boolean = true,
    red: Boolean = false
) {
    DropdownMenuItem(
            text = { Text(title) },
        onClick = onClick,
        leadingIcon = icon,
        enabled = enabled,
        colors = if (red) MenuDefaults.itemColors(
            textColor = Color.Red,
            leadingIconColor = Color.Red
        ) else MenuDefaults.itemColors()
    )
}

/**
 * Creates an "overflow" menu where minor actions are presented.
 *
 * See Compose's [DropdownMenu] and Material Design [menus](https://m3.material.io/components/menus/overview)
 * for more information.
 */
@Composable
fun MenuButton(content: @Composable ColumnScope.() -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreHoriz, contentDescription = "More actions")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            content = content
        )
    }
}