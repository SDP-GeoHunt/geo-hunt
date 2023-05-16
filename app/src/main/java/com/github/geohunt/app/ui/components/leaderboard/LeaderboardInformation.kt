package com.github.geohunt.app.ui.components.leaderboard

import androidx.compose.runtime.Composable

/**
 * Simple data class containing every parameter needed to build a Leaderboard
 * @param entries the list of entries in the leaderboard, these entries should already be sorted
 * @param userIndex the index in the leaderboard of the user seeing the leaderboard,
 *  this value can be -1 to represent that the user doesn't appear in the leaderboard
 */
data class LeaderboardInformation(val entries: List<LeaderboardEntry>, val userIndex: Int)

/**
 * One (1) entry in the leaderboard, contains all the information needed to display the entry
 * @param displayName the string displayed on the entry
 * @param score the score of the entry
 * @param displayIcon the composable function that will be called as the entries icon,
 *  can be null, representing that the entry shouldn't display anything
 */
data class LeaderboardEntry(
        val displayName: String,
        val score: Long,
        val displayIcon: DisplayIcon
)

/**
 * A nullable composable function that represents the icon
 */
typealias DisplayIcon = (@Composable () -> Unit)?
