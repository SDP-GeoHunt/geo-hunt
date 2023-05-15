package com.github.geohunt.app.ui.components.leaderboard

import androidx.compose.runtime.Composable
import com.github.geohunt.app.model.User
import com.github.geohunt.app.ui.components.user.ProfileIcon

@Composable
fun UserLeaderboard(users: List<User>, currentUser: User) {
    val currentIndex = users.indexOf(currentUser)
    Leaderboard(
            entries = users.map { toEntry(it) },
            currentIndex = currentIndex
    )
}

@Composable
fun toEntry(user: User): LeaderboardEntry {
    return LeaderboardEntry(
            displayName = user.name,
            score = 0L,
            displayIcon = { ProfileIcon(user = user) }
    )
}