package com.github.geohunt.app.ui.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.ui.components.AsyncImage
import com.github.geohunt.app.ui.rememberLazyRef
import com.github.geohunt.app.ui.theme.skeleton_loading_background

/**
 * Shows a grid of challenges
 */
@Composable
fun ChallengeGrid(challenges: List<LazyRef<Challenge>>) {
    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 128.dp)) {
        items(challenges) { challenge ->
            ChallengeItem(challenge = challenge)
        }
    }
}

@Composable
private fun ChallengeItem(challenge: LazyRef<Challenge>) {
    val c = rememberLazyRef { challenge }

    if (c.value == null) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(skeleton_loading_background))
    } else {
        AsyncImage(contentDescription = "Challenge",
            contentScale = ContentScale.Fit,
            modifier = Modifier.padding(1.dp))
        {
            c.value!!.thumbnail
        }
    }
}