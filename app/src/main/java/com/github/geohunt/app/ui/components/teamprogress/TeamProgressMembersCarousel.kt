package com.github.geohunt.app.ui.components.teamprogress

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.i18n.toSuffixedString
import com.github.geohunt.app.model.User
import com.github.geohunt.app.ui.components.utils.SkeletonLoading
import com.github.geohunt.app.ui.components.utils.SkeletonLoadingProfilePicture

/**
 * Creates the team member carousel.
 *
 * @param teamMembers The list of team members. A null value indicates loading.
 * @param onUserClick The profile picture onClick handler.
 */
@Composable
fun TeamProgressMembersCarousel(
    teamMembers: List<User?>,
    onUserClick: () -> Unit = {},
) {
    val memberWidth = 70.dp

    LazyRow(
        horizontalArrangement = spacedBy(16.dp),
        modifier = Modifier.padding(vertical = 10.dp)
    ) {
        items(teamMembers) {
            Column(
                verticalArrangement = spacedBy(5.dp)
            ) {
                SkeletonLoadingProfilePicture(
                    url = it?.profilePictureUrl,
                    size = memberWidth,
                    onClick = onUserClick,
                    contentDescription = "Profile picture of ${it?.name}"
                )

                SkeletonLoading(
                    value = it,
                    width = memberWidth,
                    height = 14.dp
                ) { user ->
                    Column(Modifier.width(memberWidth)) {
                        Text(
                            user.name,
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        // TODO Integrate with Firebase
                        Text(
                            "${10512.toSuffixedString()} pts",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }

}