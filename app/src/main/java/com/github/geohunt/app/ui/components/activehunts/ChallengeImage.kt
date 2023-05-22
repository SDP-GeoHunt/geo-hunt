package com.github.geohunt.app.ui.components.activehunts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.github.geohunt.app.model.Challenge

/**
 * The image of the challenge
 * Note that this method tries to scale the image to the exact size of the box
 * this might be changed in the future as it could distort the image
 */
@Composable
fun ChallengeImage(
        challenge: Challenge,
        modifier: Modifier
) {
    val imageUrl = challenge.photoUrl

    Box(modifier = modifier.fillMaxWidth()) { //image "frame"
        AsyncImage(
                model = imageUrl,
                contentDescription = "Challenge ${challenge.id}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(20.dp))
                        .fillMaxSize()
        )
    }
}