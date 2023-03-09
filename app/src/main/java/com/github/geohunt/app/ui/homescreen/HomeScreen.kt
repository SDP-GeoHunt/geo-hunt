package com.github.geohunt.app.ui.homescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.ui.theme.homeScreenBackground
import com.github.geohunt.app.ui.theme.whiteBackground

class HomeScreen {

    @Composable
    fun FeedScreen() {
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .background(homeScreenBackground)) {
            items(mockChallenges) { challenge ->
                ChallengeItem(challenge = challenge)
            }
        }
    }

    @Composable
    fun ChallengeItem(challenge: Challenge) {
        Column(modifier = Modifier
            .padding(16.dp, 18.dp)
            .clip(shape = RoundedCornerShape(24.dp))
            .background(whiteBackground)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RoundImageCard(image = challenge.getUser().profilePicture)
                Text(text = challenge.getUser().displayName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Image(bitmap = challenge.thumbnail.bitmap,
                contentDescription = null,
                modifier = Modifier.padding(9.dp, 9.dp),
                contentScale = ContentScale.FillWidth
            )
        }
    }

    @Composable
    fun RoundImageCard(image: Int,
                       modifier: Modifier = Modifier
                           .padding(9.dp)
                           .size(40.dp)) {
        Card(shape = CircleShape, modifier = modifier) {
            Image(painter = painterResource(id = image),
                contentDescription = null,
                contentScale = ContentScale.Crop)
        }
    }
}