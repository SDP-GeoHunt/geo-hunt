package com.github.geohunt.app.ui.homescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.R

data class MockChallenge(
    val challengeId: String,
    val challengeImg: Int,
    val username: String,
    val profilePhoto: Int,
    val likes: Int,
)

@Composable
fun HomeScreen(challenges: List<MockChallenge>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .height(60.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.header),
                contentDescription = null,
                modifier = Modifier.size(200.dp)
                    .testTag(R.drawable.header.toString())
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.White)
        ) {
            HorizontalDivider()
        }
        LazyColumn(
            modifier = Modifier
                .padding(10.dp, 10.dp)
                .background(Color.White)
        ) {
            items(challenges) { challenge ->
                ChallengeItem(challenge = challenge)
            }
        }
    }
}

@Composable
fun ChallengeItem(challenge: MockChallenge) {
    Box(
        modifier = Modifier
            .background(Color.LightGray)
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(5.dp)) {
                RoundImageCard(
                    image = challenge.profilePhoto,
                    Modifier
                        .size(48.dp)
                        .padding(3.dp)
                )
                Text(text = challenge.username, fontWeight = FontWeight.Bold)
            }
            Image(
                painter = painterResource(id = challenge.challengeImg),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .testTag(challenge.challengeImg.toString()),
                contentScale = ContentScale.FillWidth,
            )
            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.challenge_not_liked),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.size(30.dp).testTag(R.drawable.challenge_not_liked.toString())
                )
                Text(
                    text = "${challenge.likes}",
                    modifier = Modifier.padding(start = 8.dp),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun RoundImageCard(
    image: Int,
    modifier: Modifier = Modifier
        .padding(9.dp)
        .size(40.dp)
) {
    Card(shape = CircleShape, modifier = modifier) {
        Image(
            painter = painterResource(id = image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.testTag(image.toString())
        )
    }
}

@Composable
fun HorizontalDivider(
    padding : Dp = 8.dp
) {
    Divider(
        color = Color.LightGray,
        thickness = 1.dp,
        modifier = Modifier
            .alpha(0.3f)
            .padding(top = padding, bottom = padding)
    )
}