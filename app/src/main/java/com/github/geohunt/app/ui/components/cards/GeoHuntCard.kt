package com.github.geohunt.app.ui.components.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.model.User
import com.github.geohunt.app.ui.components.utils.SkeletonLoading
import com.github.geohunt.app.ui.components.utils.SkeletonLoadingProfilePicture

@Composable
fun GeoHuntCardTitle(
    author: User?,
    onUserClick: (User) -> Unit,
    subtitle: @Composable () -> Unit,
    action: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier.padding(ChallengeCardContentPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SkeletonLoadingProfilePicture(
            url = author?.profilePictureUrl,
            size = 48.dp,
            onClick = { if (author != null) onUserClick(author) },
            contentDescription = "Profile picture"
        )

        Column(
            Modifier.padding(start = ChallengeCardContentPadding),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            SkeletonLoading(value = author, width = 80.dp, height = 20.dp) { author ->
                Text(
                    author.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.clickable { onUserClick(author) }
                )
            }

            subtitle()
        }

        action?.let {
            Spacer(Modifier.weight(1.0f))
            it()
        }
    }
}