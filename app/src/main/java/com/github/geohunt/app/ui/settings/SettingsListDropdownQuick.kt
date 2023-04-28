package com.github.geohunt.app.ui.settings

import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.alorma.compose.settings.storage.base.rememberIntSettingState
import com.alorma.compose.settings.ui.SettingsList

@Composable
internal fun SettingsListDropdownQuick(
    title: String,
    items: List<String>,
    value: Int,
    icon: ImageVector,
    onItemSelected: ((Int, String) -> Unit),
    modifier: Modifier = Modifier
) {
    val state = rememberIntSettingState(value)
    state.value = value // this is used to update the settings state on every recomposition
    SettingsList(
        title = { Text(title) },
        items = items,
        state = state,
        icon = { Icon(icon, contentDescription = title) },
        onItemSelected = onItemSelected,
        modifier = modifier
    )
}