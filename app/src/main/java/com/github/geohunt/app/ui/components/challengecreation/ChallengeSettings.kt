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
import com.github.geohunt.app.i18n.DateFormatUtils
import com.github.geohunt.app.model.Challenge
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
 * Takes mutable states of the arguments it has to select
 * @param viewModel corresponding view model
 */
@Composable
fun ChallengeSettings(viewModel: CreateChallengeViewModel) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .height(120.dp)
        .padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween) {
        DifficultySelect(viewModel)

        DateSelect(viewModel)
    }
}

@Composable
private fun DifficultySelect(viewModel: CreateChallengeViewModel) {
    val difficulty = viewModel.selectedDifficulty.collectAsState()

    Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
        SettingsText(text = stringResource(id = R.string.challenge_settings_difficulty))


        ListDropdownMenu(state = difficulty.value,
            update = { viewModel.withDifficulty(it) },
            elements = Challenge.Difficulty.values().toList(),
            toString = Challenge.Difficulty::toString
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateSelect(viewModel: CreateChallengeViewModel) {
    val expirationDate = viewModel.expirationDate.collectAsState()
    val state = UseCaseState(
            embedded = false,
            onDismissRequest = { viewModel.withExpirationDate(null) }
    )

    CalendarDialog(state = state,
            selection = CalendarSelection.Date { viewModel.withExpirationDate(it) },
            config = calendarConfig())

    Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
        SettingsText(text = stringResource(id = R.string.challenge_settings_date))

        TextField(value = nullableDateToString(date = expirationDate.value),
                onValueChange = {},
                readOnly = true,
                enabled = false,
                modifier = Modifier.clickable { state.show() })
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
            fontFamily = Lobster,
            fontSize = 25.sp)
}