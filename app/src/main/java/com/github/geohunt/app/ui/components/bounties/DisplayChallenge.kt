package com.github.geohunt.app.ui.components.bounties

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.github.geohunt.app.model.Challenge

@Composable
internal fun DisplayChallenges(
    challenges: List<Challenge>,
    onCreateChallenge: () -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Challenges",
            fontSize = 22.sp,
            color = MaterialTheme.colors.primary,
            modifier = Modifier
                .padding(25.dp, 2.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            modifier = Modifier.padding(10.dp, 0.dp),
            onClick = onCreateChallenge
        ) {
            Icon(
                Icons.Default.AddCircle,
                tint = MaterialTheme.colors.primary,
                contentDescription = "Add challenge"
            )
        }
    }


    for (challenge in challenges) {
        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.61f, false)
                .padding(10.dp, 5.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("https://picsum.photos/200/300") // challenge.photoUrl)
                    .crossfade(true)
                    .build(),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                contentDescription = "challenge"
            )
        }
    }
}
