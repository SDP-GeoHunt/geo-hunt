package com.github.geohunt.app.ui.components.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.ui.components.user.ProfileIcon

/**
 * Draws a leaderboard list item.
 *
 * @param position The position of the user in the leaderboard.
 * @param user The user in the ranking.
 * @param isCurrent Whether the item corresponds to the current user shown at the bottom of the screen
 */
@Composable
fun LeaderboardListItem(
    position: Int,
    user: User,
    isCurrent: Boolean = false
) {
    require(position >= 0) { "position should be non-negative."}

    // Make the text fit for large positions
    val positionSize = when (position + 1) {
        in 1..99 -> 16. sp
        in 100..999 -> 14. sp
        else -> 12. sp
    }

    val backgroundColor = if (isCurrent) Color(0xFFFF7A00) else Color.White
    val textColor = if (isCurrent) Color.White else Color.Black

    Row(
        Modifier
            .height(54.dp)
            .background(color = backgroundColor)
            .padding(start = 8.dp, end = 20.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            (position + 1).toString(),
            modifier = Modifier.width(28. dp),
            fontSize = positionSize,
            color = textColor
        )

        ProfileIcon(user = user)

        when {
            isCurrent -> Text("You", color = textColor, fontWeight = FontWeight.SemiBold)
            else -> Text(user.name, color = textColor)
        }

        Spacer(Modifier.weight(1.0f))

        LeaderboardScore(score = user.score, color = textColor)
    }
}