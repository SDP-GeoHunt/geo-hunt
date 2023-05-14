package com.github.geohunt.app.ui.components.bounties

import android.graphics.Paint.Align
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.geohunt.app.i18n.DateFormatUtils
import com.github.geohunt.app.model.Bounty
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Team
import com.github.geohunt.app.ui.components.bounties.viewmodel.AdminBountyViewModel
import com.github.geohunt.app.ui.screens.home.HorizontalDivider
import com.github.geohunt.app.utility.DateUtils
import java.text.DateFormat
import com.github.geohunt.app.R
import java.time.LocalDateTime

@Composable
fun AdminBountyPage(
    bid: String,
    onFailure: (Throwable) -> Unit = {},
    viewModel: AdminBountyViewModel = viewModel(factory = AdminBountyViewModel.Factory)
) {
    val bountyState = viewModel.bounty.collectAsState()

    LaunchedEffect(bid, viewModel) {
        viewModel.withBusinessId(bid, onFailure)
    }

    if (bountyState.value != null) {
        val teamsState = viewModel.teamsFlow!!.collectAsState(initial = listOf())
        val challengesState = viewModel.challenges.collectAsState()

        AdminBountyPageUI(bounty = bountyState.value!!,
            teams = teamsState.value,
            challenges = challengesState.value)
    }
    else {
        CircularProgressIndicator()
    }
}

@Composable
private fun AdminBountyPageUI(
    bounty: Bounty,
    teams: List<Team>,
    challenges: List<Challenge>
) {
    val memberCount = teams.sumOf { it.membersUid.size }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp, 10.dp)
    ) {
        Row(modifier = Modifier.padding(10.dp, 0.dp)) {
            Text(
                text = bounty.name,
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.weight(1f))
            
            Icon(
                Icons.Default.AdminPanelSettings,
                contentDescription = "admin"
            )
        }
        
        Row(modifier = Modifier.padding(20.dp, 0.dp)) {
            val firstComponent = formatTimeStuff(start = bounty.startingDate, end = bounty.expirationDate)
            val secondComponent = pluralStringResource(id = R.plurals.teams_count, count = teams.size, teams.size)
            val thirdComponent = pluralStringResource(id = R.plurals.members_count, count = memberCount, memberCount)

            Text(
                text = "$firstComponent - $secondComponent - $thirdComponent",
                fontSize = 11.sp,
                color = MaterialTheme.colors.primaryVariant
            )

            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                text = DateFormatUtils.formatRange(bounty.startingDate, bounty.expirationDate),
                fontSize = 11.sp,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colors.primaryVariant
            )
        }

        HorizontalDivider(10.dp)

        DisplayTeams(teams)
    }
}

@Composable
private fun ColumnScope.DisplayTeams(
    teams: List<Team>
) {
    val orderedTeam = remember(teams) {
        teams.sortedByDescending { it.score }
    }

    Text(
        text = "Teams",
        fontSize = 22.sp,
        color = MaterialTheme.colors.primary,
        modifier = Modifier
            .align(Alignment.Start)
            .padding(25.dp, 2.dp)
    )

    for ((index, team) in orderedTeam.withIndex()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp, 2.dp)
                .height(18.dp)
        ) {
            Row {
                Text(
                    text = "#${index + 1}",
                    fontSize = 11.sp
                )

            }
        }
    }
}

@Composable
private fun formatTimeStuff(start: LocalDateTime, end: LocalDateTime) : String {
    val now = LocalDateTime.now()

    return when {
        start.isAfter(now) -> DateFormatUtils.getRemainingTimeString(
            dateTime = start,
            formattingStringId = R.string.start_in_formatter,
            passedFormattingStringId = 0
        )

        end.isAfter(now) -> DateFormatUtils.getRemainingTimeString(
            dateTime = end,
            formattingStringId = R.string.end_in_formatter,
            passedFormattingStringId = 0
        )

        else -> stringResource(id = R.string.already_terminated)
    }
}
