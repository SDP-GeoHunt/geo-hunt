package com.github.geohunt.app.ui.components.utils

import androidx.compose.material.*
import androidx.compose.runtime.*

/**
 * Creates a dropdown menu containing all the elements of elements.
 * Stores the selected value in the MutableState state to make it accessible to the caller.
 * @param state The mutableState that will be modified by the menu
 * @param elements The elements to chose from
 * @param toString The function used to convert elements to strings that will be displayed
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T> ListDropdownMenu(state: MutableState<T>, elements: Collection<T>, toString: (T) -> String) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded,
            onExpandedChange = { expanded = !expanded }) {
        TextField(value = toString(state.value),
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) })

        ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
        ) {
            elements.forEach { elem ->
                DropdownMenuItem(onClick = {
                    state.value = elem
                    expanded = false
                }) {
                    Text(text = toString(elem))
                }
            }
        }
    }
}
