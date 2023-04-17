package com.github.geohunt.app.ui.components.challengecreation

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.R
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.ui.components.utils.ListDropdownMenu
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




@Composable
fun DateSelect() {

}