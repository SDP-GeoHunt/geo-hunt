package com.github.geohunt.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.ui.components.leaderboard.*

/**
 * Creates the leaderboard view.
 *
 * @param sortedUsers The users of the leaderboard, ranked by score.
 * @param currentUser The current user viewing the leaderboard, as seen in the bottom of the screen.
 */
@Composable
fun Leaderboard(
    sortedUsers: List<User>,
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

        LazyColumn(
            Modifier.weight(1.0f)
        ) {
            itemsIndexed(sortedUsers) { index: Int, user: User ->
                when(index) {
                    in 0..2 -> {
                        LeaderboardPodiumItem(position = index, user = user)
                        Spacer(Modifier.height(10.dp))
                    }

                    else -> {
                        if (index == 3) {
                            Spacer(Modifier.height(14.dp))
                        }

                        Divider(
                            Modifier.padding(horizontal = 16. dp)
                        )
                        LeaderboardListItem(position = index, user = user)
                    }
                }
            }
        }

        LeaderboardListItem(
            position = sortedUsers.indexOf(currentUser),
            user = currentUser,
            isYou = true
        )
    }
}