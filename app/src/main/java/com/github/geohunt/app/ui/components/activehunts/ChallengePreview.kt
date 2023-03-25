package com.github.geohunt.app.ui.components.activehunts

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.ui.FetchComponent

@Composable
fun ChallengePreview(challenge: LazyRef<Challenge>) {

    FetchComponent(lazyRef = { challenge }) { resolvedChallenge ->
        Column(modifier = Modifier.fillMaxSize()) {
            ChallengeImage(challenge = resolvedChallenge)

            ChallengeDescription(challenge = resolvedChallenge)
        }
    }

}

@Composable
fun ChallengeImage(challenge: Challenge) {
    val thumbnail = challenge.thumbnail

    FetchComponent(lazyRef = { thumbnail }) {resolvedThumbnail ->
        Image(painter = BitmapPainter(resolvedThumbnail.asImageBitmap()),
                contentDescription = "Challenge ${challenge.cid}")
    }
}

@Composable
fun ChallengeDescription(challenge: Challenge) {

}