package com.github.geohunt.app.ui.components.leaderboard

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Draws the score of the user on the leaderboard
 *
 * @param score The score to be drawn.
 */
@Composable
fun LeaderboardScore(score: Number, color: Color = Color.Black) {
    Text(
        "$score pts",
        fontSize = 16.sp,
        fontWeight = FontWeight.Light,
        color = color
    )
}