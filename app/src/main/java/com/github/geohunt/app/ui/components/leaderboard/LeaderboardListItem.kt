package com.github.geohunt.app.ui.components.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.github.geohunt.app.model.database.api.User

/**
 * Draws a leaderboard list item.
 *
 * @param position The position of the user in the leaderboard.
 * @param user The user in the ranking.
 * @param isYou Whether the shown user should be sticky instead of a list item.
 *              This uses alternate styling.
 */
@Composable
fun LeaderboardListItem(
    position: Int,
    user: User,
    isYou: Boolean = false
) {
    assert(position >= 0) { "position should be non-negative."}

    // Make the text fit for large positions
    val positionSize = when (position + 1) {
        in 1..99 -> 16. sp
        in 100..999 -> 14. sp
        else -> 12. sp
    }

    val backgroundColor = if (isYou) Color(0xFFFF7A00) else Color.White
    val textColor = if (isYou) Color.White else Color.Black

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

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("https://picsum.photos/430/400") // TODO Integrate with user
                .crossfade(true)
                .build(),
            contentDescription = "${user.displayName} profile picture",
            modifier = Modifier
                .padding(8.dp)
                .clip(CircleShape)
        )

        when {
            isYou -> Text("You", color = textColor, fontWeight = FontWeight.SemiBold)
            else -> Text(user.displayName, color = textColor)
        }

        Spacer(Modifier.weight(1.0f))

        LeaderboardScore(score = user.score, color = textColor)
    }
}