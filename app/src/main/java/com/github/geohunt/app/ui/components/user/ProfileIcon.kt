package com.github.geohunt.app.ui.components.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.R
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.ui.components.AsyncImage

/**
 * Creates a round profile icon of the given size.
 *
 * @param user The user for which we draw the profile picture.
 * @param modifier The modifier to be applied to the layout
 */
@Composable
fun ProfileIcon(user: User, modifier: Modifier = Modifier, size: Dp = 60.dp) {
//    AsyncImage(
//        contentDescription = "User profile icon",
//        modifier = modifier.size(size)
//            .padding(8.dp)
//            .clip(CircleShape),
//        contentScale = ContentScale.Crop
//    ) {
//        user.profilePicture
//    }
    Image(painter = painterResource(id = R.drawable.mock_user),
        contentDescription = "profile icon",
        modifier = modifier.size(size)
            .padding(2.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop)
}