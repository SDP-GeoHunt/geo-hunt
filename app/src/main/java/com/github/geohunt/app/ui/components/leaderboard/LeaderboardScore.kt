package com.github.geohunt.app.ui.components.leaderboard

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.github.geohunt.app.i18n.toSuffixedString

/**
 * Draws the score of the user on the leaderboard
 *
 * @param score The score to be drawn.
 * @param color The color of the text.
 */
@Composable
fun LeaderboardScore(score: Long, color: Color = Color.Black) {
    Text(
        "${score.toSuffixedString()} pts",
        fontSize = 16.sp,
        fontWeight = FontWeight.Light,
        color = color
    )
}