package com.github.geohunt.app.ui.screens.teamleaderboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.geohunt.app.ui.components.leaderboard.Leaderboard

/**
 * A leaderboard for teams in a bounty, simply calls Leaderboard with the arguments given by the view model
 * @param bid the bounty id we want the leaderboard of
 * @param viewModel the underlying UserLeaderboardViewModel
 */
@Composable
fun TeamLeaderboard(bid: String, viewModel: TeamLeaderboardViewModel = viewModel(factory = TeamLeaderboardViewModel.factory(bid))) {
    val leaderboardInformation = viewModel.leaderboardInformation.collectAsState()

    Leaderboard(
            leaderboardInformation = leaderboardInformation.value
    )
}