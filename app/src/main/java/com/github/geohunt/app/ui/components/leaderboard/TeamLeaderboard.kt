package com.github.geohunt.app.ui.components.leaderboard

import androidx.compose.runtime.Composable
import com.github.geohunt.app.model.Team

@Composable
fun TeamLeaderboard(teams: List<Team>, currentTeam: Team) {
    val currentIndex = teams.indexOf(currentTeam)
    Leaderboard(
            entries = teams.map { toEntry(it) },
            currentIndex = currentIndex
    )
}

@Composable
fun toEntry(team: Team): LeaderboardEntry {
    return LeaderboardEntry(
            displayName = team.name,
            score = team.score,
            displayIcon = null
    )
}