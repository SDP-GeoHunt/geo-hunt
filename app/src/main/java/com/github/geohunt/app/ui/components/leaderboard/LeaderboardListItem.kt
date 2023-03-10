package com.github.geohunt.app.ui.components.leaderboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.github.geohunt.app.model.database.api.User

/**
 * Draws a leaderboard list item.
 *
 * @param position The position of the user in the leaderboard.
 * @param user The user in the ranking
 */
@Composable
fun LeaderboardListItem(position: Int, user: User) {
    assert(position >= 0) { "position should be non-negative."}

    // Make the text fit for large positions
    val positionSize = when (position + 1) {
        in 1..99 -> 16. sp
        in 100..999 -> 14. sp
        else -> 12. sp
    }

    Row(
        Modifier
            .height(54.dp)
            .padding(start = 8.dp, end = 20.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            (position + 1).toString(),
            modifier = Modifier.width(28. dp),
            fontSize = positionSize
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

        Text(
            user.displayName ?: ("@" + user.uid)
        )

        Spacer(Modifier.weight(1.0f))

        LeaderboardScore(score = user.score)
    }
}