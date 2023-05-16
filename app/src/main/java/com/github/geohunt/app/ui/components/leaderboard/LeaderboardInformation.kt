package com.github.geohunt.app.ui.components.leaderboard

import androidx.compose.runtime.Composable

data class LeaderboardInformation(val entries: List<LeaderboardEntry>, val userIndex: Int)

data class LeaderboardEntry(
        val displayName: String,
        val score: Long,
        val displayIcon: DisplayIcon
)

typealias DisplayIcon = (@Composable () -> Unit)?
