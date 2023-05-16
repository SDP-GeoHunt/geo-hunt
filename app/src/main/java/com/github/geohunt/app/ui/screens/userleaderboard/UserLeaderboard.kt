package com.github.geohunt.app.ui.screens.userleaderboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.geohunt.app.ui.components.leaderboard.Leaderboard

@Composable
fun UserLeaderboard(viewModel: UserLeaderboardViewModel = viewModel(factory = UserLeaderboardViewModel.Factory)) {
    val leaderboardInformation = viewModel.leaderboardInformation.collectAsState()

    Leaderboard(
            entries = leaderboardInformation.value.entries,
            currentIndex = leaderboardInformation.value.userIndex
    )
}