package com.github.geohunt.app.ui.screens.userleaderboard

import com.github.geohunt.app.data.repository.ScoreRepositoryInterface
import com.github.geohunt.app.data.repository.UserRepositoryInterface
import com.github.geohunt.app.model.User

class UserLeaderboardViewModel(
        scoreRepository: ScoreRepositoryInterface,
        userRepository: UserRepositoryInterface
) {
    val entries = listOf<Pair<User, Long>>()
}