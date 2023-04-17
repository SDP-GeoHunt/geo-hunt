package com.github.geohunt.app.ui.components.challengecreation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.geohunt.app.R
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.ui.components.utils.ListDropdownMenu
import com.github.geohunt.app.ui.theme.Lobster
import com.maxkeppeker.sheets.core.models.base.UseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import java.time.LocalDate

/**
 * Screen to select the settings of a challenge,
 * for now this includes :
 *  - Challenge difficulty
 *  - Challenge expiration date
 */
@Composable
fun ChallengeSettings(selectedDifficulty: MutableState<Challenge.Difficulty>, selectedDate: MutableState<LocalDate?>) {
    Column(modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
            verticalArrangement = Arrangement.SpaceBetween) {
        DifficultySelect(selectedDifficulty)

        DateSelect(selectedDate)
    }
}

@Composable
fun DifficultySelect(selectedDifficulty: MutableState<Challenge.Difficulty>) {
    Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically) {
        SettingsText(text = stringResource(id = R.string.challenge_settings_difficulty))

        ListDropdownMenu(state = selectedDifficulty,
                elements = Challenge.Difficulty.values().toList(),
                toString = Challenge.Difficulty::toString)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelect(selectedDate: MutableState<LocalDate?>) {
    val state = UseCaseState(
            onDismissRequest = { selectedDate.value = null },
            onCloseRequest = {}
    )
    CalendarDialog(state = state,
            selection = CalendarSelection.Date { selectedDate.value = it },
            config = calendarConfig())

    Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically) {
        SettingsText(text = stringResource(id = R.string.challenge_settings_date))

        TextField(value = selectedDate.value?.toString() ?: stringResource(id = R.string.challenge_settings_date_never),
                onValueChange = {},
                readOnly = true,
                enabled = false,
                modifier = Modifier.clickable { state.show() })
    }
}

fun calendarConfig(): CalendarConfig {
    val now = LocalDate.now()
    return CalendarConfig(
            boundary = now .. now.plusMonths(2)
    )
}

@Composable
fun SettingsText(text: String) {
    Text(text = text,
            fontFamily = Lobster,
            fontSize = 25.sp)
}