package com.github.geohunt.app.ui.components.challenge

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.geohunt.app.R
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Claim
import com.github.geohunt.app.ui.FetchComponent
import com.github.geohunt.app.ui.components.AsyncImage
import com.github.geohunt.app.ui.components.LabelledIcon
import com.github.geohunt.app.i18n.DateFormatUtils
import com.github.geohunt.app.i18n.toSuffixedString

@Composable
fun ClaimCard(claimRef: LazyRef<Claim>, displayImage: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.8f, false)
            .padding(5.dp)
    ) {
        FetchComponent(
            lazyRef = { claimRef },
            modifier = Modifier.fillMaxSize()
        ) { claim ->
            Column {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.Black)
                ) {
                    AsyncImage(
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxHeight()
                            .align(Alignment.Center)
                            .clickable {
                                displayImage(claim.image.id)
                            },
                        contentDescription = "claimed image"
                    ) {
                        claim.image
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 2.dp)
                        .height(39.dp)
                ) {
                    FetchComponent(lazyRef = { claim.user }) { user ->
                        AsyncImage(
                            contentDescription = "User",
                            modifier = Modifier
                                .size(35.dp)
                                .clip(CircleShape)
                        ) {
                            user.profilePicture
                        }

                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = user.name,
                                fontSize = 16.sp
                            )

                            Row {
                                Text(
                                    text = DateFormatUtils.getElapsedTimeString(
                                        claim.time,
                                        R.string.claimed_format
                                    ) + " · ",
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(start = 10.dp),
                                    color = MaterialTheme.colors.primary
                                )

                                LabelledIcon(
                                    text = claim.distance.toSuffixedString() + "m",
                                    painter = painterResource(id = R.drawable.ruler_measure),
                                    contentDescription = "Distance",
                                    fontColor = MaterialTheme.colors.primary,
                                    fontSize = 11.sp,
                                    iconSize = 11.dp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp)
                        ) {
                            Text(
                                text = "+300",
                                fontSize = 16.sp,
                                color = Color(R.color.md_theme_light_tertiary)
                            )

                            LabelledIcon(
                                text = user.score.toInt().toSuffixedString(),
                                painter = painterResource(id = R.drawable.cards_diamond),
                                contentDescription = "Total points",
                                fontSize = 13.sp,
                                iconSize = 13.dp
                            )
                        }
                    }
                }
            }
        }
    }
}