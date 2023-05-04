package com.github.geohunt.app.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.github.geohunt.app.R
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.User
import kotlinx.coroutines.flow.StateFlow

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)) {
    val challenges = viewModel.challengeFeed.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
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
                modifier = Modifier
                    .size(200.dp)
                    .testTag(R.drawable.header.toString())
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.White)
        ) {
            HorizontalDivider(padding = 0.dp)
        }

        when(challenges.value) {
            null -> Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            else -> LazyColumn {
                items(challenges.value!!) { challenge ->
                    ChallengeItem(
                        challenge = challenge,
                        getAuthor = viewModel::getAuthor,
                        photoUrl = viewModel.getChallengePhoto(challenge)
                    )

                    Box(modifier = Modifier.fillMaxWidth().height(8.dp).background(Color(0xFFF1F3F4))) {

                    }
                }
            }
        }
    }
}

@Composable
fun ChallengeItem(
    challenge: Challenge,
    getAuthor: (Challenge) -> StateFlow<User?>,
    photoUrl: String
) {
    val author = getAuthor(challenge).collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(5.dp)) {
            when(val v = author.value) {
                null -> CircularProgressIndicator()
                else -> {
                    RoundImageCard(
                        imageUrl = v.profilePictureUrl,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(3.dp)
                    )
                    Text(text = v.displayName ?: v.id, fontWeight = FontWeight.Bold)
                }
            }
        }
        AsyncImage(
            model = photoUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(18.dp))
                .testTag(photoUrl),
            contentScale = ContentScale.FillWidth,
        )
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.thumb_up_outline),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .padding(4.dp)
                    .size(24.dp)
                    .testTag(R.drawable.thumb_up_outline.toString())
            )
            Text(
                // text = "${challenge.likes}",
                text = "0",
                modifier = Modifier.padding(start = 8.dp),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun RoundImageCard(
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
    Card(shape = CircleShape, modifier = modifier) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.testTag(imageUrl ?: "")
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