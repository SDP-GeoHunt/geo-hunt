package com.github.geohunt.app.ui.components.bounties

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.github.geohunt.app.model.Challenge

@Composable
internal fun ColumnScope.DisplayChallenges(
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
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp, 5.dp)
        ) {
            Column {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(challenge.photoUrl)
                        .build(),
                    contentDescription = "challenge"
                )

                IconButton(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = { /*TODO*/ }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete challenge")
                }
            }
        }
    }
}
