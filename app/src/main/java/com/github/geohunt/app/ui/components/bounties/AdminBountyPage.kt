package com.github.geohunt.app.ui.components.bounties

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.geohunt.app.R
import com.github.geohunt.app.i18n.DateFormatUtils
import com.github.geohunt.app.i18n.toSuffixedString
import com.github.geohunt.app.model.Bounty
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Team
import com.github.geohunt.app.ui.components.bounties.viewmodel.AdminBountyViewModel
import com.github.geohunt.app.ui.screens.home.HorizontalDivider
import java.time.LocalDateTime

@Composable
fun AdminBountyPage(
    bid: String,
    onFailure: (Throwable) -> Unit = {},
    onCreateChallenge: () -> Unit = {},
    viewModel: AdminBountyViewModel = viewModel(factory = AdminBountyViewModel.Factory)
) {
    val bountyState = viewModel.bounty.collectAsState()

    LaunchedEffect(bid, viewModel) {
        viewModel.withBusinessId(bid, onFailure)
    }

    if (bountyState.value != null) {
        val teamsState = viewModel.teamsFlow!!.collectAsState(initial = listOf())
        val challengesState = viewModel.challenges.collectAsState()

        AdminBountyPageUI(
            bounty = bountyState.value!!,
            teams = teamsState.value,
            challenges = challengesState.value,
            setName = viewModel::setBountyName,
            onCreateChallenge = onCreateChallenge
        )
    } else {
        CircularProgressIndicator()
    }
}

@Composable
private fun AdminBountyPageUI(
    bounty: Bounty,
    setName: (String) -> Unit,
    teams: List<Team>,
    challenges: List<Challenge>,
    onCreateChallenge: () -> Unit,
) {
    val memberCount = teams.sumOf { it.membersUid.size }
    var showPopup by remember { mutableStateOf(false) }

    if (showPopup) {
        RenameBountyPopup(bounty) { newNameOpt ->
            showPopup = false
            newNameOpt?.apply { setName(this) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(5.dp, 10.dp)
            .testTag("admin-bounty-page-loaded")
    ) {
        Row(modifier = Modifier.padding(10.dp, 0.dp)) {
            Text(
                text = bounty.name,
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = { showPopup = true },
                modifier = Modifier
                    .testTag("edit-btn")
                    .align(Alignment.CenterVertically)) {
                Icon(Icons.Default.Edit,
                    contentDescription = "edit")
            }

            Spacer(modifier = Modifier.width(5.dp))

            Icon(Icons.Default.AdminPanelSettings,
                modifier = Modifier.align(Alignment.CenterVertically),
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

        Spacer(modifier = Modifier.height(10.dp))

        DisplayChallenges(challenges, onCreateChallenge)
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
        val fontSize = 15.sp
        val (bg, cl) = when (index) {
            0 -> (MaterialTheme.colors.primary to MaterialTheme.colors.onPrimary)
            1 -> (MaterialTheme.colors.secondary to MaterialTheme.colors.onSecondary)
            2 -> (MaterialTheme.colors.primaryVariant to MaterialTheme.colors.onPrimary)
            else -> (MaterialTheme.colors.surface to MaterialTheme.colors.onSurface)
        }

        Card(
            shape = RoundedCornerShape(2.dp),
            backgroundColor = bg,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp, 2.dp)
                .height(24.dp)
        ) {
            Row {
                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    text = "#${index + 1}",
                    fontSize = fontSize,
                    color = cl
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = team.name,
                    fontSize = fontSize,
                    color = cl,
                    maxLines = 1,
                    modifier = Modifier.width(100.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Icon(Icons.Default.Person, contentDescription = "Person")

                Text(
                    text = team.membersUid.size.toString(),
                    modifier = Modifier.width(40.dp),
                    color = cl,
                    fontSize = fontSize
                )

                Spacer(modifier = Modifier.weight(1f))

                Icon(
                    painter = painterResource(id = R.drawable.cards_diamond),
                    contentDescription = "Diamond",
                    tint = cl
                )

                Text(
                    text = team.score.toSuffixedString(),
                    modifier = Modifier.width(40.dp),
                    color = cl,
                    fontSize = fontSize
                )

                Spacer(modifier = Modifier.width(5.dp))
            }
        }
    }
}



@Composable
private fun formatTimeStuff(start: LocalDateTime, end: LocalDateTime): String {
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
