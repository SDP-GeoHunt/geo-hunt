package com.github.geohunt.app.ui.components.challenge

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.geohunt.app.R
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.ui.FetchComponent
import com.github.geohunt.app.ui.components.LabelledIcon
import com.github.geohunt.app.ui.components.user.ProfileIcon
import com.github.geohunt.app.i18n.DateFormatUtils
import com.github.geohunt.app.i18n.toSuffixedString
import com.github.geohunt.app.model.database.api.LoggedUserContext
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.ui.rememberLazyRef

@Composable
internal fun LoggedUserContext.MainUserView(challenge: Challenge) {
    val isSelf = challenge.author.isLoggedUser

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp, 2.dp)
            .height(70.dp)
            .clipToBounds()
    ) {
        FetchComponent(
            lazyRef = { challenge.author },
            modifier = Modifier.align(Alignment.Center)
        ) { author ->

            Row {
                ProfileIcon(
                    user = author,
                    modifier = Modifier.testTag("profile-icon").size(70.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Row {
                        Text(
                            text = author.name,
                            fontSize = 20.sp,
                            maxLines = 1,
                            textAlign = TextAlign.Left,
                            modifier = Modifier
                                .align(Alignment.Bottom)
                                .padding(2.dp)
                                .wrapContentSize(unbounded = true)
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        LabelledIcon(
                            text = author.score.toInt().toSuffixedString(),
                            painter = painterResource(id = R.drawable.cards_diamond),
                            tint = Color(R.color.md_theme_light_tertiary),
                            contentDescription = "Card diamond",
                            fontColor = MaterialTheme.colors.primaryVariant,
                            fontSize = 19.sp,
                            iconSize = 20.dp,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(end = 15.dp)
                        )
                    }

                    Row {
                        Text(
                            text = DateFormatUtils.getElapsedTimeString(
                                challenge.publishedDate,
                                R.string.published_format
                            ),
                            fontSize = 10.sp,
                            color = MaterialTheme.colors.primaryVariant,
                            textAlign = TextAlign.Left,
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .align(Alignment.Top)
                                .wrapContentSize(unbounded = true)
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        if (!isSelf) {
                            FollowButton(
                                author = author,
                                modifier = Modifier
                                    .size(63.dp, 24.dp)
                                    .align(Alignment.CenterVertically),
                            )

                            IconButton(
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .testTag("btn-notification"),
                                onClick = { /*TODO*/ }
                            ) {
                                Icon(
                                    Icons.Rounded.Notifications,
                                    contentDescription = "Notification bell"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun LoggedUserContext.FollowButton(author: User, modifier: Modifier = Modifier) {
    val doesFollow = rememberLazyRef {
        author.doesFollow
    }

    Button(
        modifier = modifier,
        contentPadding = PaddingValues(2.dp, 2.dp),
        shape = RoundedCornerShape(12.dp),
        enabled = doesFollow.value != null,
        onClick = {
            if (author.doesFollow.value != null) {
                if (author.doesFollow.value!!) {
                    author.unfollow()
                } else {
                    author.follow()
                }
            }
        })
    {
        FetchComponent(lazyRef = { author.doesFollow }) { doesFollow ->
            Text(
                text = if (doesFollow) "Unfollow" else "Follow",
                fontSize = 10.sp
            )
        }
    }
}
