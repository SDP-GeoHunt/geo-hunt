package com.github.geohunt.app.ui.components.challengecreation

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.R
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.ui.theme.Lobster

/**
 * Screen to select the settings of a challenge,
 * for now this includes :
 *  - Challenge difficulty
 *  - Challenge expiration date
 */
@Composable
fun ChallengeSettings(selectedDifficulty: MutableState<Challenge.Difficulty>) {
    Column(modifier = Modifier.fillMaxWidth().height(40.dp),
            verticalArrangement = Arrangement.SpaceAround) {
        DifficultySelect(selectedDifficulty)

        DateSelect()
    }
}

@Composable
fun DifficultySelect(selectedDifficulty: MutableState<Challenge.Difficulty>) {

    Box(modifier = Modifier.fillMaxSize()) {
        Row() {
            Text(text = stringResource(id = R.string.challenge_settings_difficulty), fontFamily = Lobster)

            ListDropdownMenu(state = selectedDifficulty,
                    elements = Challenge.Difficulty.values().toList(),
                    toString = Challenge.Difficulty::toString)
        }
    }
}


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


@Composable
fun DateSelect() {

}