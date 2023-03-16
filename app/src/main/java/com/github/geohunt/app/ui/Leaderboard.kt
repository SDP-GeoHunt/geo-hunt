package com.github.geohunt.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.ui.components.leaderboard.LeaderboardList
import com.github.geohunt.app.ui.components.leaderboard.LeaderboardListItem
import com.github.geohunt.app.ui.components.leaderboard.LeaderboardPodiumItem
import com.github.geohunt.app.ui.components.leaderboard.LeaderboardTitleBar

/**
 * Creates the leaderboard view.
 * 
 * The top 3 users are given special styling (see [LeaderboardPodiumItem]).
 *
 * @param users The users of the leaderboard, ranked by score.
 * @param currentUser The current user viewing the leaderboard, as seen in the bottom of the screen.
 */
@Composable
fun Leaderboard(
    users: List<User>,
    currentUser: User
) {
    Column {
        // Wrap in a column to have minimal spacing with the chips
        Column(
            Modifier.padding(horizontal = 16.dp)
        ) {
            LeaderboardTitleBar()

            // Uncomment this line when/if different leaderboards are implemented
            // This is for a future user story...
            // LeaderboardChips()
        }

        // Note that the modifier argument can not be removed, as [Modifier.weight] is an extension
        // method only available in a [ColumnScope] or [RowScope]
        LeaderboardList(users = users, Modifier.weight(1.0f))

        // Bottom "You" item
        LeaderboardListItem(
            position = users.indexOf(currentUser),
            user = currentUser,
            isCurrent = true
        )
    }
}