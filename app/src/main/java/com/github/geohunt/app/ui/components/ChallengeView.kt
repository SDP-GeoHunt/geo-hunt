package com.github.geohunt.app.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.github.geohunt.app.R
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Claim
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.ui.homescreen.HorizontalDivider
import com.github.geohunt.app.ui.rememberLazyRef
import com.github.geohunt.app.utility.DateFormatUtils.getElapsedTimeString
import com.github.geohunt.app.utility.toSuffixedString

private const val lorumIpsum =
    """Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc bibendum enim non iaculis malesuada. Praesent non accumsan eros. Ut ut eros dolor. Interdum et malesuada fames ac ante ipsum primis in faucibus. Phasellus scelerisque eros nec porta facilisis. Mauris quis consectetur libero, nec vestibulum leo. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc vulputate ipsum eu felis consectetur mollis. Proin varius dui felis, sit amet mattis nibh sollicitudin et. Nunc semper felis tortor, quis tempus sapien porta eget. Proin convallis est nec mauris efficitur posuere. Nam sit amet varius tellus, et mollis enim. Sed vel ligula imperdiet, cursus arcu eget, consequat turpis. Suspendisse potenti.

Praesent bibendum non dolor eu fringilla. Etiam ac lorem sit amet quam auctor volutpat. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Fusce accumsan laoreet tellus, vel eleifend tortor venenatis eget. Suspendisse fermentum tellus eget vestibulum tincidunt. Donec sed tempus libero. Vestibulum pellentesque tempus sodales. Suspendisse eros risus, egestas nec porta et, pulvinar at lorem. Nulla a ante sed enim pretium vehicula ut ac eros. Nullam sollicitudin justo eu est sagittis, at vulputate mauris interdum. Sed non tellus interdum, placerat velit nec, pharetra magna."""

@Composable
private fun MainUserView(challenge: Challenge, author: User?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp, 2.dp)
            .height(70.dp)
            .clipToBounds()
    ) {
        if (author != null) {
            Row {
                Image(
                    painter = painterResource(R.drawable.mock_user),
                    contentDescription = "User profile picture",
                    modifier = Modifier
                        .size(70.dp)
                        .align(Alignment.Top)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(15.dp))

                Column(
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Row {
                        Text(
                            text = author.displayName ?: author.name,
                            fontSize = 7.em,
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
                            fontSize = 4.em,
                            iconSize = 20.dp,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(end = 15.dp)
                        )
                    }

                    Row {
                        Text(
                            text = getElapsedTimeString(challenge.publishedDate, R.string.published_format),
                            fontSize = 3.em,
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
                            Text(
                                text = "Follow",
                                fontSize = 3.em
                            )
                        }

                        IconButton(
                            modifier = Modifier.align(Alignment.CenterVertically),
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
        else {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }

    }
}

@Composable
fun BellowImageButtons(challenge: Challenge) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp, 5.dp)
    ) {
        val fontSize = 5.em
        val iconSize = 22.dp

        LabelledIcon(
            text = "42",
            painter = painterResource(R.drawable.likes),
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
            contentDescription = "Claims",
            fontSize = fontSize,
            iconSize = iconSize
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
            contentDescription = "Points",
            fontSize = fontSize,
            iconSize = iconSize
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            modifier = Modifier
                .size(80.dp, 28.dp)
                .align(Alignment.CenterVertically),
            contentPadding = PaddingValues(2.dp, 2.dp),
            shape = RoundedCornerShape(12.dp),
            onClick = { /*TODO*/ })
        {
            Text(
                text = "Join",
                fontSize = 4.em
            )
        }
    }
}

@Composable
fun ClaimCard(claim: Claim) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .aspectRatio(1.8f, false)
            ) {
                AsyncImage(
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.Center),
                    contentDescription = "claimed image") {
                    claim.image
                }
            }

            Row(
                modifier = Modifier.padding(horizontal = 20.dp,
                    vertical = 2.dp)
            ) {
                val user = rememberLazyRef {
                    claim.user
                }

                if (user.value != null) {
                    AsyncImage(contentDescription = "User",
                        modifier = Modifier
                            .size(35.dp)
                            .clip(CircleShape))
                    {
                        user.value!!.profilePicture
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(text = user.value!!.name,
                            fontSize = 5.em)

                        Row {
                            Text(text = getElapsedTimeString(claim.time, R.string.claimed_format) + " · ",
                                fontSize = 2.em,
                                modifier = Modifier.padding(start = 10.dp),
                                color = MaterialTheme.colors.primary
                            )

                            LabelledIcon(text = claim.distance.toInt().toSuffixedString() + "m",
                                painter = painterResource(id = R.drawable.ruler_measure),
                                contentDescription = "Distance",
                                fontColor = MaterialTheme.colors.primary,
                                fontSize = 2.em,
                                iconSize = 11.dp)
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp)
                    ) {
                        Text(
                            text = "+300",
                            fontSize = 4.em,
                            color = Color(R.color.md_theme_light_tertiary)
                        )

                        LabelledIcon(text = "300k",
                            painter = painterResource(id = R.drawable.cards_diamond),
                            contentDescription = "Total points",
                            fontSize = 3.em,
                            iconSize = 13.dp)
                    }
                }
                else {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }

        }
    }
}

@Composable
fun ChallengeView(challenge : Challenge) {
    val author = rememberLazyRef {
        challenge.author
    }

    val user = object : User {
        override val uid: String = "uid"
        override var displayName: String? = "John Smith"
        override val profilePicture: LazyRef<Bitmap>
            get() = TODO()
        override val challenges: List<LazyRef<Challenge>>
            get() = TODO()
        override val hunts: List<LazyRef<Challenge>>
            get() = TODO()
        override var score: Number
            get() = 34580
            set(value) {}
    }

    val isDescriptionExpanded = remember {
        mutableStateOf(false)
    }

    val lazyState = rememberLazyListState()
    val transition = updateTransition(remember { derivedStateOf { lazyState.firstVisibleItemIndex != 0 } }, label = "Image size transition")

    val imageAspectRatio by transition.animateFloat(label = "Animate image size ratio") { isScrolling ->
        if (isScrolling.value) 1.8f else 1.0f
    }

    Box {
        Column {
            AsyncImage(contentDescription = "Challenge Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(imageAspectRatio, false),
                contentScale = ContentScale.Crop) {
                challenge.thumbnail
            }

            BellowImageButtons(challenge)

            Spacer(Modifier.height(2.dp))

            HorizontalDivider(
                padding = 2.dp
            )

            LazyColumn(
                state = lazyState
            ) {
                item {
                    Spacer(modifier = Modifier.height(1.dp))
                }

                item {
                    MainUserView(challenge = challenge, author = author.value)
                    HorizontalDivider(padding = 2.dp)
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = if (isDescriptionExpanded.value) Int.MAX_VALUE.dp else 90.dp)
                            .padding(10.dp),
                        elevation = 10.dp
                    ) {
                        Column(
                            verticalArrangement = Arrangement.SpaceAround
                        ) {
                            Text(
                                lorumIpsum,
                                fontSize = 3.em,
                                color = MaterialTheme.colors.onBackground,
                                maxLines = if (isDescriptionExpanded.value) Int.MAX_VALUE else 2,
                                modifier = Modifier.padding(10.dp),
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = if (isDescriptionExpanded.value) "less..." else "more...",
                                fontSize = 3.em,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(bottom = 5.dp)
                                    .clickable {
                                        isDescriptionExpanded.value = !isDescriptionExpanded.value
                                    }
                            )
                        }
                    }
                }

                items(challenge.claims.size) { index : Int ->
                    val claim = rememberLazyRef {
                        challenge.claims[index]
                    }

                    if (claim.value != null) {
                        ClaimCard(claim = claim.value!!)
                    }
                    else {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                                .aspectRatio(0.5f, false)
                        ) {

                        }
                    }
                }
            }
        }

        IconButton(
            modifier = Modifier
                .size(48.dp)
                .padding(10.dp),
            onClick = { /*TODO*/ }) {
            Icon(
                Icons.Rounded.ArrowBack,
                contentDescription = "Go back"
            )
        }
    }
}
