package com.github.geohunt.app.ui.components.leaderboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


typealias DisplayIcon = (@Composable () -> Unit)?
data class LeaderboardEntry(
        val displayName: String,
        val score: Long,
        val displayIcon: DisplayIcon
)

/**
 * Creates the leaderboard view.
 * 
 * The top 3 users are given special styling (see [LeaderboardPodiumItem]).
 *
 * @param entries The entries of the leaderboard, ranked by score.
 * @param currentIndex The index of the current entry, as seen in the bottom of the screen.
 */
@Composable
fun Leaderboard(
    entries: List<LeaderboardEntry>,
    currentIndex: Int
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
        // TODO: Integrate with view model
        LeaderboardList(entries = entries, Modifier.weight(1.0f))

        // Bottom "You" item
        LeaderboardListItem(
            position = currentIndex,
            entry = entries[currentIndex],
            isCurrent = true,
        )
    }
}