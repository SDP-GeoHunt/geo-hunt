package com.github.geohunt.app.ui.components.leaderboard

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.model.database.api.User

/**
 * Creates a scrollable leaderboard, with the first 3 users having special
 * style items.
 *
 * @param users The list of users in the leaderboard
 */
@Composable
fun LeaderboardList(users: List<User>) {
    LazyColumn {
        itemsIndexed(users) { index: Int, user: User ->
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
}