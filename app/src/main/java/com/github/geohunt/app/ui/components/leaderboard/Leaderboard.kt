package com.github.geohunt.app.ui.components.leaderboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Creates the leaderboard view.
 * 
 * The top 3 users are given special styling (see [LeaderboardPodiumItem]).
 *
 * @param leaderboardInformation the data class containing all the information about the Leaderboard
 *  * The entries to display
 *  * The position of given user in the leaderboard (or -1 if he isn't in the leaderboard,
 *  he won't get displayed)
 */
@Composable
fun Leaderboard(
    leaderboardInformation: LeaderboardInformation
) {
    val entries = leaderboardInformation.entries
    val currentIndex = leaderboardInformation.userIndex
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
        LeaderboardList(entries = entries, Modifier.weight(1.0f))

        // Bottom "You" item
        if(currentIndex >= 0) {
            LeaderboardListItem(
                    position = currentIndex,
                    entry = entries[currentIndex],
                    isCurrent = true,
            )
        }
    }
}