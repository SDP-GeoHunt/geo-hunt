package com.github.geohunt.app.ui.components.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.geohunt.app.R
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.ui.components.user.ProfileIcon
import com.github.geohunt.app.ui.theme.Lobster
import com.github.geohunt.app.ui.theme.geoHuntRed

/**
 * Creates a podium list item for the top 3 users.
 * The position argument affects the exact UI of the item, including the background color and the
 * height of the item.
 *
 * @param user The user info that is printed on the screen.
 * @param position A number between 0 and 2 indicating the user's ranking.
 */
@Composable
fun LeaderboardPodiumItem(user: User, position: Int) {
    require(position in 0..2) { "Position in PodiumItem should be in 0..2" }

    val height = arrayOf(100. dp, 70. dp, 70. dp)[position]
    val opacity = (80 - (20 * position)) / 100.0f

    Row(
        Modifier
            .padding(horizontal = 16. dp)
            .clip(CircleShape)
            .background(color = geoHuntRed.copy(alpha = opacity))
            .height(height)
            .fillMaxWidth()
    ) {
        ProfileIcon(user = user)

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
                user.displayName,
                fontSize = 18. sp,
                color = Color.White
            )

            if (position == 0) {
                LeaderboardScore(user.score, color = Color.White)
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
                LeaderboardScore(user.score, color = Color.White)
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