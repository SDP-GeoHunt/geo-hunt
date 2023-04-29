package com.github.geohunt.app.ui.components.challenge

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.geohunt.app.R
import com.github.geohunt.app.authentication.Authenticator
import com.github.geohunt.app.i18n.toSuffixedString
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.Database
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.ui.FetchComponent
import com.github.geohunt.app.ui.components.LabelledIcon
import com.github.geohunt.app.ui.rememberLazyRef
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.suspendCoroutine

@Composable
internal fun BellowImageButtons(challenge: Challenge, database: Database, user: User) {
    val currentUser = rememberLazyRef {
        database.getUserById(Authenticator.authInstance.get().user!!.uid)
    }

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(15.dp, 5.dp)) {
        val fontSize = 18.sp
        val iconSize = 22.dp
        
        LikeButton(challenge = challenge,
            database = database,
            user = user,
            fontSize = fontSize,
            iconSize = iconSize)

        Spacer(modifier = Modifier
            .width(18.dp)
            .weight(0.2f))

        LabelledIcon(
            text = challenge.claims.size.toString(),
            painter = painterResource(R.drawable.target_arrow),
            contentDescription = "Claims", fontSize = fontSize, iconSize = iconSize)

        Spacer(modifier = Modifier
            .width(18.dp)
            .weight(0.2f))

        LabelledIcon(
            text = "+25",
            painter = painterResource(R.drawable.cards_diamond),
            tint = Color(R.color.md_theme_light_tertiary),
            contentDescription = "Scores", fontSize = fontSize, iconSize = iconSize
        )

        Spacer(modifier = Modifier.weight(1f))

        if (currentUser.value != null)
        {
            val hasJoined = currentUser.value!!.activeHunts.any { it.id == challenge.cid }

            Button(
                modifier = Modifier
                    .size(80.dp, 28.dp)
                    .align(Alignment.CenterVertically),
                contentPadding = PaddingValues(2.dp, 2.dp),
                shape = RoundedCornerShape(12.dp),
                onClick = {
                    GlobalScope.launch {
                        if (hasJoined) {
                            database.leaveHunt(challenge.cid)
                        }
                        else {
                            database.joinHunt(challenge.cid)
                        }
                    }
                })
            {
                Text(
                    text = if (hasJoined) stringResource(R.string.leave_hunt)
                                     else stringResource(R.string.join_hunt),
                    fontSize = 17.sp
                )
            }
        }
    }
}

@Composable
internal fun LikeButton(challenge: Challenge, database: Database, user: User, fontSize: TextUnit, iconSize: Dp) {
    val hasUserLikedChallenge: LazyRef<Boolean> = database.doesUserLike(user.uid, challenge.cid)
    val numberOfLikes = remember { challenge.likes.size }

    FetchComponent(
        lazyRef = { hasUserLikedChallenge },
    ) { defaultLiked ->
        var isLiked by remember {
            mutableStateOf(defaultLiked)
        }
        val coroutineScope = rememberCoroutineScope()

        IconButton(
            onClick = {
                if (isLiked) {
                    coroutineScope.launch {
                        database.removeUserLike(
                            user.uid,
                            challenge.cid
                        )
                    }
                    isLiked = false

                } else {
                    coroutineScope.launch {
                        database.insertUserLike(
                            user.uid,
                            challenge.cid
                        )
                    }
                    isLiked = true
                }
            }
        ) {
            LabelledIcon(
                text = numberOfLikes.toSuffixedString(),
                painter = painterResource(if (isLiked) R.drawable.thumb_up_filled else R.drawable.thumb_up_outline),
                contentDescription = "Likes",
                fontSize = fontSize,
                iconSize = iconSize,
            )
        }
    }
}