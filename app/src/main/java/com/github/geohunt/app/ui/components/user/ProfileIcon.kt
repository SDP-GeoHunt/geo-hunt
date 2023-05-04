package com.github.geohunt.app.ui.components.user


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.github.geohunt.app.model.User

/**
 * Creates a round profile icon of the given size.
 *
 * @param user The user for which we draw the profile picture.
 * @param modifier The modifier applied to the profile picture.
 */
@Composable
fun ProfileIcon(user: User, modifier: Modifier = Modifier) {
    val newModifier = modifier
        .aspectRatio(1f)
        .padding(8.dp)
        .clip(CircleShape)


    if (user.profilePictureUrl != null) {
        ProfileIcon(user.profilePictureUrl, user.displayName, modifier = newModifier)
    } else {
        DefaultProfileIcon(modifier = newModifier)
    }
}

@Composable
fun DefaultProfileIcon(modifier: Modifier) {
    Icon(
        imageVector = Icons.Default.Person,
        contentDescription = "No profile picture",
        modifier = modifier
            .padding(16.dp)
            .background(MaterialTheme.colors.surface),
    )
}

@Composable
fun ProfileIcon(source: Any?, username: String?, modifier: Modifier = Modifier) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(source)
            .crossfade(true)
            .build(),
        contentDescription = "$username profile picture",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .aspectRatio(1f)
            .padding(8.dp)
            .clip(CircleShape)
    )
}