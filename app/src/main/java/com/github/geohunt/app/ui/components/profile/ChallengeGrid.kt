package com.github.geohunt.app.ui.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.ui.rememberLazyRef
import com.github.geohunt.app.ui.theme.skeleton_loading_background

@Composable
fun ChallengeGrid(challenges: List<LazyRef<Challenge>>) {
    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 128.dp)) {
        items(challenges) { challenge ->
            run {
                ChallengeItem(challenge = challenge)
            }
        }
    }
}

@Composable
fun ChallengeItem(challenge: LazyRef<Challenge>) {
    val c = rememberLazyRef { challenge }

    if (c.value == null) {
        Box(modifier = Modifier.fillMaxSize().background(skeleton_loading_background))
    } else {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("https://picsum.photos/430/400") // TODO Integrate with file store
                .size(Size.ORIGINAL)
                .crossfade(true)
                .build(),
            contentDescription = "Challenge",
            modifier = Modifier
                .padding(8.dp)
        )
    }
}