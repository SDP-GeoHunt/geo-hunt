package com.github.geohunt.app.ui.components.user

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.ui.rememberLazyRef

/**
 * Creates a round profile icon of the given size.
 *
 * @param user The user for which we draw the profile picture.
 */
@Composable
fun ProfileIcon(user: User) {
    val profilePicture = rememberLazyRef { user.profilePicture }

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(profilePicture.value)
            .crossfade(true)
            .build(),
        contentDescription = "${user.name} profile picture",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .aspectRatio(1f)
            .padding(8.dp)
            .clip(CircleShape)
    )
}