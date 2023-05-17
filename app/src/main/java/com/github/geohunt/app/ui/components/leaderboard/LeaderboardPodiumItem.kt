package com.github.geohunt.app.ui.components.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.geohunt.app.R
import com.github.geohunt.app.ui.theme.Lobster
import com.github.geohunt.app.ui.theme.geoHuntRed

/**
 * Creates a podium list item for the top 3 users.
 * The position argument affects the exact UI of the item, including the background color and the
 * height of the item.
 *
 * @param entry The entry that is printed on the screen.
 * @param position A number between 0 and 2 indicating the user's ranking.
 */
@Composable
fun LeaderboardPodiumItem(entry: LeaderboardEntry, position: Int) {
    require(position in 0..2) { "Position in PodiumItem should be in 0..2" }

    val height = arrayOf(100. dp, 70. dp, 70. dp)[position]
    val opacity = (90 - (15 * position)) / 100.0f

    Row(
        Modifier
            .padding(horizontal = 16. dp)
            .clip(CircleShape)
            .background(color = geoHuntRed.copy(alpha = opacity))
            .height(height)
            .fillMaxWidth()
    ) {
        if (entry.displayIcon != null) {
            //We only display the icon if there is one
            entry.displayIcon.invoke()
        }
        else {
            Icon(
                Icons.Rounded.Star,
                contentDescription = "Leaderboard position",
                modifier = Modifier.aspectRatio(1f).padding(8.dp).clip(CircleShape),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "${position + 1}.",
                fontSize = 30. sp,
                color = Color.White,
                fontFamily = Lobster,
                modifier = Modifier.height(38. dp).padding(top = 3. dp)
            )
            Text(
                entry.displayName,
                fontSize = 18. sp,
                color = Color.White
            )

            if (position == 0) {
                LeaderboardScore(entry.score, color = Color.White)
            }
        }

        Spacer(Modifier.weight(1.0f))

        Column(
            Modifier
                .fillMaxHeight()
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            if (position != 0) {
                LeaderboardScore(entry.score, color = Color.White)
            } else {
                Icon(
                    painter = painterResource(R.drawable.baseline_local_fire_department_24),
                    contentDescription = "Fire !",
                    modifier = Modifier.size(48. dp),
                    tint = Color.White
                )
            }
        }
    }
}