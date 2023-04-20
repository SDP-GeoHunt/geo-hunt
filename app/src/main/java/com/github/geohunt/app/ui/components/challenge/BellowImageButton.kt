package com.github.geohunt.app.ui.components.challenge

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
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
import com.github.geohunt.app.i18n.toSuffixedString
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.LoggedUserContext
import com.github.geohunt.app.ui.FetchComponent
import com.github.geohunt.app.ui.components.LabelledIcon
import com.github.geohunt.app.ui.rememberLazyRef

@Composable
internal fun LoggedUserContext.BellowImageButtons(
    challenge: Challenge,
    fnClaimCallback: (String) -> Unit
) {
    val doesUserFollowThisChallenge = rememberLazyRef(null, { loggedUserRef }) { user ->
        user.activeHunts.any { it.id == challenge.cid }
    }
    val numberOfLikes = remember { challenge.likes.size }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp, 5.dp)
    ) {
        val fontSize = 18.sp
        val iconSize = 22.dp

        LabelledIcon(
            text = numberOfLikes.toSuffixedString(),
            painter = painterResource(R.drawable.thumb_up_filled),
            contentDescription = "Likes",
            fontSize = fontSize,
            iconSize = iconSize
        )

        Spacer(
            modifier = Modifier
                .width(18.dp)
                .weight(0.2f)
        )

        LabelledIcon(
            text = challenge.claims.size.toString(),
            painter = painterResource(R.drawable.target_arrow),
            contentDescription = "Claims", fontSize = fontSize, iconSize = iconSize
        )

        Spacer(
            modifier = Modifier
                .width(18.dp)
                .weight(0.2f)
        )

        LabelledIcon(
            text = "+25",
            painter = painterResource(R.drawable.cards_diamond),
            tint = Color(R.color.md_theme_light_tertiary),
            contentDescription = "Scores", fontSize = fontSize, iconSize = iconSize
        )

        Spacer(modifier = Modifier.weight(1f))

        LikeButton(
            challenge = challenge,
            modifier = Modifier.size(23.dp).align(Alignment.CenterVertically)
        )

        Spacer(
            modifier = Modifier
                .width(18.dp)
                .weight(0.2f)
        )

        if (doesUserFollowThisChallenge == true) {
            IconButton(
                onClick = { challenge.leaveHunt() },
                modifier = Modifier
                    .size(23.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.disable_favorite_inactive_svgrepo_com),
                    contentDescription = "Unfollow",
                    tint = MaterialTheme.colors.primary
                )
            }
        } else {
            IconButton(
                onClick = { challenge.joinHunt() }, modifier = Modifier
                    .size(23.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.star_svgrepo_com),
                    contentDescription = "Follow",
                    tint = MaterialTheme.colors.primary
                )
            }
        }

        Spacer(
            modifier = Modifier
                .width(18.dp)
                .weight(0.2f)
        )

        Button(
            modifier = Modifier
                .size(70.dp, 28.dp)
                .align(Alignment.CenterVertically),
            contentPadding = PaddingValues(2.dp, 2.dp),
            shape = RoundedCornerShape(12.dp),
            onClick = { fnClaimCallback(challenge.cid) })
        {
            Text(
                text = "Claim",
                fontSize = 15.sp
            )
        }
    }
}

@Composable
internal fun LoggedUserContext.LikeButton(challenge: Challenge, modifier: Modifier = Modifier) {
    val hasUserLikedChallenge = challenge.doesLoggedUserLikes

    FetchComponent(
        lazyRef = { hasUserLikedChallenge },
    ) { defaultLiked ->
        var isLiked by remember {
            mutableStateOf(defaultLiked)
        }

        IconButton(
            onClick = {
                isLiked = if (isLiked) {
                    challenge.unlike()
                    false
                } else {
                    challenge.like()
                    true
                }
            }, modifier = modifier
        ) {
            Icon(
                painter = painterResource(id = if (isLiked) R.drawable.thumb_up_filled else R.drawable.thumb_up_outline),
                contentDescription = "Like",
                tint = MaterialTheme.colors.primary
            )
        }
    }
}