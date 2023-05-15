package com.github.geohunt.app.ui.screens.view_bounty

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person3
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.geohunt.app.R
import com.github.geohunt.app.model.Team
import com.github.geohunt.app.model.User
import com.github.geohunt.app.ui.components.GoBackBtn
import com.github.geohunt.app.ui.components.user.ProfileIcon

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ViewBountyPage(
    bountyId: String,
    onBack: () -> Any,
    viewModel: ViewBountyViewModel = viewModel(factory = ViewBountyViewModel.getFactory(bountyId))
) {
    val challenges by viewModel.challenges.collectAsState()
    val teams by viewModel.teams.collectAsState()
    val users by viewModel.users.collectAsState()
    val isBusy by viewModel.isBusy.collectAsState()
    val currentTeam by viewModel.currentTeam.collectAsState()

    Box {
        GoBackBtn(fnGoBackCallback = { onBack() })
        challengesImageSlider(challenges, modifier = Modifier
            .fillMaxWidth()
            .height(128.dp))

        TeamsSelector(
            teams = teams,
            users = users,
            join = { viewModel.joinTeam(it.teamId) },
            leaveTeam = { viewModel.leaveCurrentTeam() },
            disabled = isBusy,
            currentTeam = currentTeam
        )

        TeamCreator(
            createTeam = { viewModel.createOwnTeam(it) },
            disabled = isBusy
        )
    }
}

@Composable
fun TeamCreator(createTeam: (String) -> Unit, disabled: Boolean) {
    val name = remember { mutableStateOf("") }
    Row {
        TextField(
            value = name.value, onValueChange = { name.value = it },
            singleLine = true,
            placeholder = { Text(text = stringResource(id = R.string.enter_team_name)) },
            label = { Text(text = stringResource(id = R.string.team_name)) },
            enabled = !disabled
        )
        TextButton(
            onClick = { createTeam(name.value) },
            enabled = !disabled
        ) {
            Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.create_team))
        }
    }
}

@Composable
fun TeamsSelector(
    teams: List<Team>?,
    users: Map<String, List<User>>,
    join: (Team) -> Any,
    leaveTeam: () -> Any,
    disabled: Boolean,
    currentTeam: Team?
) {
    if (teams == null) return

    LazyColumn {
        items(teams) { team ->
            if (team == currentTeam)
                TeamSelector(team.name, users = users[team.teamId], { leaveTeam() }, disabled, true)
            else
                TeamSelector(
                    team.name,
                    users = users[team.teamId],
                    { join(team) },
                    disabled || currentTeam != null, // Disable if the user is already inside a team
                    false
                )
        }
    }
}

@Composable
fun TeamSelector(
    name: String,
    users: List<User>?,
    join: () -> Any,
    disabled: Boolean,
    shouldShowLeaveBtn: Boolean
) {
    Column {
        Row {
            Icon(Icons.Default.Person3, contentDescription = null)
            Text(name)
            Spacer(Modifier.weight(1f))

            IconButton(onClick = { join() }, enabled = !disabled) {
                when(shouldShowLeaveBtn) {
                    false -> Icon(Icons.Default.PersonAdd, contentDescription = stringResource(id = R.string.join_team))
                    true -> Icon(Icons.Default.Logout, contentDescription = stringResource(id = R.string.leave_team))
                }

            }
        }

        users?.let { users ->
            LazyColumn {
                items(users) {
                    Row(horizontalArrangement = Arrangement.Center) {
                        ProfileIcon(user = it)
                        Text(text = it.name)
                    }
                }
            }
        }


    }
}