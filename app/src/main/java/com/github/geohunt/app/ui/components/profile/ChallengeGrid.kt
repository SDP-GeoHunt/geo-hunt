package com.github.geohunt.app.ui.components.profile

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.github.geohunt.app.model.Challenge

/**
 * Shows a grid of challenges
 */
@Composable
fun ChallengeGrid(challenges: List<Challenge>) {
    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 128.dp)) {
        items(challenges) { challenge ->
            ChallengeItem(challenge = challenge)
        }
    }
}

@Composable
private fun ChallengeItem(challenge: Challenge) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(challenge.photoUrl)
            .size(Size.ORIGINAL)
            .crossfade(true)
            .build(),
        contentDescription = "Challenge",
        modifier = Modifier
            .padding(8.dp)
    )
}