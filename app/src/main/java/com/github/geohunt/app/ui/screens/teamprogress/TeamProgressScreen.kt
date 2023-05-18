package com.github.geohunt.app.ui.screens.teamprogress

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.geohunt.app.ui.screens.teamprogress.TeamProgressViewModel.TeamStatus
import com.github.geohunt.app.ui.utils.pagination.FinitePagedList

/**
 * Creates the team progress screen.
 */
@Composable
fun TeamProgressScreen(
    onBack: () -> Unit,
    onLeaderboard: (String) -> Unit,
    onChat: (String) -> Unit,
    bountyId: String,
    viewModel: TeamProgressViewModel = viewModel(factory = TeamProgressViewModel.getFactory(bountyId))
) {
    val teamStatus = viewModel.teamStatus.collectAsStateWithLifecycle()

    when(teamStatus.value) {
        TeamStatus.LOADING_TEAM -> EmptyTeamProgressScreen(
            title = "Loading...",
            onBack = onBack
        ) {
            CircularProgressIndicator()
        }

        TeamStatus.ERROR_NO_TEAM -> EmptyTeamProgressScreen(
            title = "Error",
            onBack = onBack
        ) {
            Box(modifier = Modifier
                .padding(it)
                .fillMaxSize()) {
                Text(
                    "It seems that you are not registered in a team yet. " +
                            "Create your team on the bounty page by clicking the back button.",
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .align(Alignment.Center)
                )
            }
        }

        TeamStatus.LOADED_TEAM -> {
            val teamName = viewModel.teamName.collectAsStateWithLifecycle()
            val challenges = viewModel.challenges.collectAsStateWithLifecycle()
            val hunters = viewModel.claimState.collectAsStateWithLifecycle()

            TeamProgressScreenContent(
                onBack = onBack,
                onLeaderboard = { onLeaderboard(bountyId) },
                onChat = { onChat(bountyId) },
                onClaim = {},
                teamName = teamName.value!!,
                teamMembers = viewModel.teamMembers,
                claimState = hunters.value ?: FinitePagedList.empty(),
                newMessages = viewModel.newMessages,
                locationState = viewModel.currentLocation,
                challenges = challenges.value
            )
        }
    }
}
