package com.github.geohunt.app.ui.components.challengecreation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.geohunt.app.R
import com.github.geohunt.app.i18n.DateFormatUtils
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.ui.components.utils.ListDropdownMenu
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
 * Takes mutable states of the arguments it has to select
 * @param selectedDifficulty Mutable state of a challenge difficulty that can be modified by user interaction
 * @param selectedDate Mutable state of a LocalDate that can be modified by user interaction.
 *  Can be null representing that there is no expiration date
 */
@Composable
fun ChallengeSettings(selectedDifficulty: MutableState<Challenge.Difficulty>, selectedDate: MutableState<LocalDate?>) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .height(120.dp)
        .padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween) {
        DifficultySelect(selectedDifficulty)

        DateSelect(selectedDate)
    }
}

@Composable
fun DifficultySelect(selectedDifficulty: MutableState<Challenge.Difficulty>) {
    Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
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
            embedded = false,
            onDismissRequest = { selectedDate.value = null }
    )
    CalendarDialog(state = state,
            selection = CalendarSelection.Date { selectedDate.value = it },
            config = calendarConfig())

    Row(modifier = Modifier.fillMaxWidth()
        .padding(10.dp, 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
        SettingsText(text = stringResource(id = R.string.challenge_settings_date))

        Spacer(modifier = Modifier.weight(0.8f))
        
        TextField(value = nullableDateToString(date = selectedDate.value),
                onValueChange = {},
                readOnly = true,
                enabled = false,
                textStyle = TextStyle(fontSize = 13.sp),
                modifier = Modifier
                    .height(50.dp)
                    .clickable { state.show() })
    }
}

private fun calendarConfig(): CalendarConfig {
    val now = LocalDate.now()
    return CalendarConfig(
            boundary = now .. now.plusMonths(2) //Hardcoded value which might be replaced later
    )
}

@Composable
private fun nullableDateToString(date: LocalDate?): String {
    if(date == null) return stringResource(id = R.string.challenge_settings_date_never)
    return DateFormatUtils.formatDate(date)
}

@Composable
fun SettingsText(text: String) {
    Text(text = text,
         fontSize = 14.sp)
}