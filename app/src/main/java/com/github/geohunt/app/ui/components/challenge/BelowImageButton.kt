package com.github.geohunt.app.ui.components.challenge

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.geohunt.app.R
import com.github.geohunt.app.i18n.toSuffixedString
import com.github.geohunt.app.ui.components.LabelledIcon

@Composable
internal fun BelowImageButton(
    joinHunt: () -> Unit,
    leaveHunt: () -> Unit,
    fnClaimHuntCallback: (String) -> Unit,
    state: ChallengeViewModel.State
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp, 5.dp)
    ) {
        val fontSize = 18.sp
        val iconSize = 22.dp

        LabelledIcon(text = state.claims.size.toString(),
            painter = painterResource(R.drawable.target_arrow),
            contentDescription = "Claims",
            fontSize = fontSize,
            iconSize = iconSize)

        Spacer(modifier = Modifier
            .width(18.dp)
            .weight(0.2f))

        LabelledIcon(
            text = "+" + state.claims.sumOf { it.awardedPoints }.toSuffixedString(),
            painter = painterResource(R.drawable.cards_diamond),
            tint = Color(R.color.md_theme_light_tertiary),
            contentDescription = "Scores", fontSize = fontSize, iconSize = iconSize
        )

        Spacer(modifier = Modifier.weight(1f))

        if (!state.isSelf)
        {
            val doesHunt = state.doesHunt.collectAsState(initial = false)

            Button(
                modifier = Modifier
                    .height(28.dp)
                    .align(Alignment.CenterVertically),
                contentPadding = PaddingValues(10.dp, 2.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !state.alreadyClaimed,
                onClick = {
                    if (doesHunt.value) fnClaimHuntCallback(state.challenge.id)
                    else joinHunt()
                })
            {
                Text(
                    text = stringResource(id = if (doesHunt.value) R.string.claim_hunt else R.string.join_hunt),
                    fontSize = 14.sp
                )
            }

            if (doesHunt.value) {
                IconButton(onClick = { leaveHunt() }) {
                    Icon(
                        Icons.Rounded.Cancel,
                        contentDescription = "Leave hunt"
                    )
                }
            }
        }
    }
}

//@Composable
//internal fun LikeButton(challenge: Challenge, database: Database, user: User, fontSize: TextUnit, iconSize: Dp) {
//    val hasUserLikedChallenge: LazyRef<Boolean> = database.doesUserLike(user.uid, challenge.cid)
//    val numberOfLikes = remember { challenge.likes.size }
//
//    FetchComponent(
//        lazyRef = { hasUserLikedChallenge },
//    ) { defaultLiked ->
//        var isLiked by remember {
//            mutableStateOf(defaultLiked)
//        }
//        val coroutineScope = rememberCoroutineScope()
//
//        IconButton(
//            onClick = {
//                if (isLiked) {
//                    coroutineScope.launch {
//                        database.removeUserLike(
//                            user.uid,
//                            challenge.cid
//                        )
//                    }
//                    isLiked = false
//
//                } else {
//                    coroutineScope.launch {
//                        database.insertUserLike(
//                            user.uid,
//                            challenge.cid
//                        )
//                    }
//                    isLiked = true
//                }
//            }
//        ) {
//            LabelledIcon(
//                text = numberOfLikes.toSuffixedString(),
//                painter = painterResource(if (isLiked) R.drawable.thumb_up_filled else R.drawable.thumb_up_outline),
//                contentDescription = "Likes",
//                fontSize = fontSize,
//                iconSize = iconSize,
//            )
//        }
//    }
//}