package com.github.geohunt.app.ui.components.utils

import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import kotlinx.coroutines.flow.StateFlow

/**
 * Creates a dropdown menu containing all the elements of elements.
 * Stores the selected value in the MutableState state to make it accessible to the caller.
 * @param state The mutableState that will be modified by the menu
 * @param update Called whenever the value gets updated
 * @param elements The elements the user can chose from
 * @param toString The function used to convert elements to strings that will be displayed
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T> ListDropdownMenu(state: T, update: (T) -> Unit, elements: Collection<T>, toString: (T) -> String) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.testTag("dropdown_menu_box")) {

        PreviewText(value = toString(state), expanded = expanded)

        ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
        ) {
            elements.forEach { elem ->
                DropdownMenuItem(onClick = {
                    update(elem)
                    expanded = false
                }, modifier = Modifier.testTag("dropdown_menu_item_" + toString(elem))) {
                    Text(text = toString(elem))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PreviewText(value: String, expanded: Boolean) {
    TextField(value = value,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.testTag("dropdown_menu_text_field"))
}
