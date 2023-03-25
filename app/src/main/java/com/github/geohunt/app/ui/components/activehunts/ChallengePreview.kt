package com.github.geohunt.app.ui.components.activehunts

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.ui.rememberLazyRef

@Composable
fun ChallengePreview(challenge: LazyRef<Challenge>) {
    val waitingChallenge = rememberLazyRef { challenge }

    if(waitingChallenge.value == null) {
        CircularProgressIndicator()
    }
    else {
        Column(modifier = Modifier.fillMaxSize()) {
            val resolvedChallenge = waitingChallenge.value!!

            ChallengeImage(challenge = resolvedChallenge)

            ChallengeDescription(challenge = resolvedChallenge)
        }
    }
}

@Composable
fun ChallengeImage(challenge: Challenge) {
    val thumbnail = challenge.thumbnail
    val waitingThumbnail = rememberLazyRef { thumbnail }

    if(waitingThumbnail.value == null) {
        CircularProgressIndicator()
    }
    else {
        val resolvedThumbnail = waitingThumbnail.value!!

        Image(painter = BitmapPainter(resolvedThumbnail.asImageBitmap()),
                contentDescription = "Challenge ${challenge.cid}")
    }
}

@Composable
fun ChallengeDescription(challenge: Challenge) {

}