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

/**
 * Draws the main leaderboard list.
 *
 * The top 3 users are given special styling (see [LeaderboardPodiumItem]).
 *
 * @param entries The entries of the leaderboard
 * @param modifier The modifier to apply to the [LazyColumn] scrollable list.
 */
@Composable
fun LeaderboardList(entries: List<LeaderboardEntry>, modifier: Modifier) {
    LazyColumn(
        modifier = modifier
    ) {
        itemsIndexed(entries) { index: Int, entry: LeaderboardEntry ->
            when(index) {
                in 0..2 -> {
                    LeaderboardPodiumItem(position = index, entry = entry)
                    Spacer(Modifier.height(10.dp))
                }

                else -> {
                    if (index == 3) {
                        Spacer(Modifier.height(14.dp))
                    }

                    Divider(
                        Modifier.padding(horizontal = 16. dp)
                    )
                    LeaderboardListItem(position = index, entry = entry)
                }
            }
        }
    }
}