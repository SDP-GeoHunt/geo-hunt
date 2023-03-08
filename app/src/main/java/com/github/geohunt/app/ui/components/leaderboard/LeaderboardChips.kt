package com.github.geohunt.app.ui.components.leaderboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.ui.theme.geoHuntRed

enum class LeaderboardChoice(val text: String) {
    GLOBAL("Global"),
    FRIENDS("Friends")
}

/**
 * Draws the leaderboard chips used to select between global and friends
 * leaderboards.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LeaderboardChips() {
    var selectedLeaderboard by remember {
        mutableStateOf(LeaderboardChoice.GLOBAL)
    }

    @Composable
    fun LeaderboardChip(choice: LeaderboardChoice, isSelected: Boolean) {
        FilterChip(
            onClick = { selectedLeaderboard = choice },
            selected = isSelected,
            colors = ChipDefaults.filterChipColors(
                selectedBackgroundColor = geoHuntRed,
                selectedContentColor = Color.White,
                selectedLeadingIconColor = Color.White
            )
        ) {
            Text(choice.text)
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(10. dp)
    ) {
        LeaderboardChip(LeaderboardChoice.GLOBAL, isSelected = selectedLeaderboard == LeaderboardChoice.GLOBAL)
        LeaderboardChip(LeaderboardChoice.FRIENDS, isSelected = selectedLeaderboard == LeaderboardChoice.FRIENDS)
    }
}