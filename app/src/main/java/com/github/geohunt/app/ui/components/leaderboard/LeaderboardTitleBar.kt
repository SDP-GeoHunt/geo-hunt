package com.github.geohunt.app.ui.components.leaderboard

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.github.geohunt.app.ui.theme.Lobster

/**
 * Draws the leaderboard title, as well as the time filter on the top-right
 * of the bar.
 */
@Composable
fun LeaderboardTitleBar() {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Leaderboard",
            fontSize = 40. sp,
            fontFamily = Lobster
        )

        //Removed because it is too ambitious to implement with our current architecture (and time)
        //Spacer(Modifier.weight(1.0f))
        //LeaderboardTimeFilter()
    }
}