package com.github.geohunt.app.ui.components.challenge

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.geohunt.app.R
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.Database
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.ui.FetchComponent
import com.github.geohunt.app.ui.components.LabelledIcon

@Composable
internal fun BellowImageButtons(challenge: Challenge, database: Database, user: User) {
    Row(modifier = Modifier.fillMaxWidth().padding(15.dp, 5.dp)) {
        val fontSize = 18.sp
        val iconSize = 22.dp

        LikeButton(challenge = challenge,
            database = database,
            user = user,
            fontSize = fontSize,
            iconSize = iconSize)

        Spacer(modifier = Modifier.width(18.dp).weight(0.2f))

        LabelledIcon(
            text = challenge.claims.size.toString(),
            painter = painterResource(R.drawable.target_arrow),
            contentDescription = "Claims", fontSize = fontSize, iconSize = iconSize)

        Spacer(modifier = Modifier.width(18.dp).weight(0.2f))

        LabelledIcon(
            text = "+25",
            painter = painterResource(R.drawable.cards_diamond),
            tint = Color(R.color.md_theme_light_tertiary),
            contentDescription = "Scores", fontSize = fontSize, iconSize = iconSize
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            modifier = Modifier.size(80.dp, 28.dp).align(Alignment.CenterVertically),
            contentPadding = PaddingValues(2.dp, 2.dp),
            shape = RoundedCornerShape(12.dp),
            onClick = { /*TODO*/ })
        {
            Text(
                text = "Join",
                fontSize = 17.sp
            )
        }
    }
}

@Composable
internal fun LikeButton(challenge: Challenge, database: Database, user: User, fontSize: TextUnit, iconSize: Dp) {
    val hasUserLikedChallenge: LazyRef<Boolean> = database.isUserLiked(user.uid, challenge.cid)
    var numberOfLikes by remember { mutableStateOf(challenge.nbLikes ) }

    FetchComponent(
        lazyRef = { hasUserLikedChallenge },
    ) { liked ->
        var isLiked = liked

        IconButton(
            onClick = {
                if (isLiked) {
                    database.removeUserLike(
                        user.uid,
                        challenge.cid
                    )
                    challenge.nbLikes -= 1
                    isLiked = false

                } else {
                    database.insertUserLike(
                        user.uid,
                        challenge.cid
                    )
                    isLiked = true
                    challenge.nbLikes += 1
                }

                numberOfLikes = challenge.nbLikes
            }
        ) {
            LabelledIcon(
                text = numberOfLikes.toString(),
                painter = painterResource(R.drawable.challenge_like),
                tint = if (isLiked) Color.Yellow else Color.Black,
                contentDescription = "Likes",
                fontSize = fontSize,
                iconSize = iconSize,
                modifier = Modifier.padding(end = 15.dp)
            )
        }
    }
}