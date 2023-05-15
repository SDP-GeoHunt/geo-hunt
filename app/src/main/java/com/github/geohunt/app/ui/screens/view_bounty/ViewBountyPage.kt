package com.github.geohunt.app.ui.screens.view_bounty

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.geohunt.app.R
import com.github.geohunt.app.model.Team
import com.github.geohunt.app.model.User
import com.github.geohunt.app.ui.components.navigation.TopBarWithBackButton
import com.github.geohunt.app.ui.components.user.ProfileIcon

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ViewBountyPage(
    bountyId: String,
    onBack: () -> Any,
    onSelectedTeam: () -> Any,
    viewModel: ViewBountyViewModel = viewModel(factory = ViewBountyViewModel.getFactory(bountyId))
) {
    val bountyName by viewModel.bountyName.collectAsState()
    val challenges by viewModel.challenges.collectAsState()
    val teams by viewModel.teams.collectAsState()
    val users by viewModel.users.collectAsState()
    val isBusy by viewModel.isBusy.collectAsState()
    val currentTeam by viewModel.currentTeam.collectAsState()
    val canDeleteTeams by viewModel.canDeleteTeams.collectAsState()

    Column {
        TopBarWithBackButton(onBack = { onBack() }, title = stringResource(
            id = R.string.select_team_for_bounty,
            bountyName ?: "…"
        ), {
            IconButton(onClick = { onSelectedTeam() }, enabled = currentTeam != null) {
                Icon(Icons.Default.Check, contentDescription = null)
            }
        })

        Box {
            challengesImageSlider(
                challenges, modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp)
            )
        }

        Scaffold(
            bottomBar = {
                TeamCreator(
                    createTeam = { viewModel.createOwnTeam(it) },
                    disabled = isBusy || currentTeam != null
                )
            }
        ) {
            TeamsSelector(
                teams = teams,
                users = users,
                join = { viewModel.joinTeam(it.teamId) },
                leaveTeam = { viewModel.leaveCurrentTeam() },
                disabled = isBusy,
                currentTeam = currentTeam,
                canDeleteTeams = canDeleteTeams,
                onDeleteTeam = { viewModel.deleteTeam(it) },
                modifier = Modifier.padding(it)
            )
        }
    }
}

@Composable
fun TeamCreator(createTeam: (String) -> Unit, disabled: Boolean) {
    val name = remember { mutableStateOf("") }

    Surface(elevation = 4.dp) {
        Column {
            Text(text = stringResource(id = R.string.create_team))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                TextField(
                    value = name.value, onValueChange = { name.value = it },
                    singleLine = true,
                    placeholder = { Text(text = stringResource(id = R.string.enter_team_name)) },
                    label = { Text(text = stringResource(id = R.string.team_name)) },
                    enabled = !disabled
                )
                TextButton(
                    onClick = { createTeam(name.value); name.value = "" },
                    enabled = !disabled
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.create_team))
                }
            }
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
    currentTeam: Team?,
    canDeleteTeams: List<String>,
    onDeleteTeam: (Team) -> Any,
    modifier: Modifier = Modifier
) {
    if (teams == null) return

    LazyColumn(modifier = modifier.padding(8.dp)) {
        items(teams) { team ->
            if (team == currentTeam)
                TeamSelector(team.name, users = users[team.teamId], { leaveTeam() }, disabled, true, canDelete = false, {})
            else
                TeamSelector(
                    team.name,
                    users = users[team.teamId],
                    { join(team) },
                    disabled || currentTeam != null, // Disable if the user is already inside a team
                    false,
                    canDeleteTeams.contains(team.teamId) && currentTeam?.teamId != team.teamId, // Can not delete if he's already inside the team
                    onDelete = { onDeleteTeam(team) }
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
    shouldShowLeaveBtn: Boolean,
    canDelete: Boolean,
    onDelete: () -> Any
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.padding(4.dp))
            Text(name)
            Spacer(Modifier.weight(1f))

            if (canDelete) {
                IconButton(onClick = { onDelete() }) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(id = R.string.delete_team))
                }
                Spacer(Modifier.size(4.dp))
            }

            IconButton(onClick = { join() }, enabled = !disabled) {
                when(shouldShowLeaveBtn) {
                    false -> Icon(Icons.Default.PersonAdd, contentDescription = stringResource(id = R.string.join_team))
                    true -> Icon(Icons.Default.Logout, contentDescription = stringResource(id = R.string.leave_team))
                }

            }
        }

        users?.let { users ->
            users.forEach {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProfileIcon(user = it, modifier = Modifier.size(64.dp))
                    Text(text = it.name)
                }
            }
        }
    }
}