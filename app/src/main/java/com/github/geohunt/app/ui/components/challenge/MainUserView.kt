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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.geohunt.app.R
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.ui.FetchComponent
import com.github.geohunt.app.ui.components.LabelledIcon
import com.github.geohunt.app.ui.components.user.ProfileIcon
import com.github.geohunt.app.i18n.DateFormatUtils
import com.github.geohunt.app.i18n.toSuffixedString

@Composable
internal fun MainUserView(challenge: Challenge) {
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
                    size = 70.dp,
                    modifier = Modifier.testTag("profile-icon")
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Row {
                        Text(
                            text = author.name,
                            fontSize = 27.sp,
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

                        Button(
                            modifier = Modifier
                                .size(63.dp, 24.dp)
                                .align(Alignment.CenterVertically),
                            contentPadding = PaddingValues(2.dp, 2.dp),
                            shape = RoundedCornerShape(12.dp),
                            onClick = { /*TODO*/ })
                        {
                            Text(text = "Follow", fontSize = 11.sp)
                        }

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
