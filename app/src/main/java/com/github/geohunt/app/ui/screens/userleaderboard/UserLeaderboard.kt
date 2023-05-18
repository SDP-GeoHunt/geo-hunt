package com.github.geohunt.app.ui.screens.userleaderboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.geohunt.app.ui.components.leaderboard.Leaderboard

/**
 * A leaderboard for users, simply calls Leaderboard with the arguments given by the view model
 * @param viewModel the underlying UserLeaderboardViewModel
 */
@Composable
fun UserLeaderboard(viewModel: UserLeaderboardViewModel = viewModel(factory = UserLeaderboardViewModel.Factory)) {
    val leaderboardInformation = viewModel.leaderboardInformation.collectAsState()

    Leaderboard(
            leaderboardInformation = leaderboardInformation.value
    )
}