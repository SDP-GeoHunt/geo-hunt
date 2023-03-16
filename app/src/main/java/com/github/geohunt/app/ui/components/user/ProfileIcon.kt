package com.github.geohunt.app.ui.components.user

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.github.geohunt.app.model.database.api.User

/**
 * Creates a round profile icon of the given size.
 *
 * @param user The user for which we draw the profile picture.
 * @param size The size of the icon.
 */
@Composable
fun ProfileIcon(user: User, size: Size = Size.ORIGINAL) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data("https://picsum.photos/430/400") // TODO Integrate with user
            .size(size)
            .crossfade(true)
            .build(),
        contentDescription = "${user.name} profile picture",
        modifier = Modifier
            .padding(8.dp)
            .clip(CircleShape)
    )
}