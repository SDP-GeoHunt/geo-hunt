package com.github.geohunt.app.ui.components.teamprogress

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.geohunt.app.model.User
import com.github.geohunt.app.ui.components.utils.SkeletonLoading
import com.github.geohunt.app.ui.components.utils.SkeletonLoadingProfilePicture
import com.github.geohunt.app.ui.utils.pagination.FinitePagedList

/**
 * Creates the team member carousel.
 *
 * @param teamMembers The list of team members. A null value indicates loading.
 * @param onUserClick The profile picture onClick handler.
 */
@Composable
fun TeamProgressMembersCarousel(
    teamMembers: FinitePagedList<User>,
    onUserClick: () -> Unit = {},
) {
    val memberWidth = 70.dp

    LazyRow(
        horizontalArrangement = spacedBy(16.dp),
        modifier = Modifier.padding(vertical = 10.dp)
    ) {
        items(teamMembers.size()) {
            val member = teamMembers.get(it).collectAsStateWithLifecycle()

            Column(
                verticalArrangement = spacedBy(5.dp)
            ) {
                SkeletonLoadingProfilePicture(
                    url = member.value?.profilePictureUrl,
                    size = memberWidth,
                    onClick = onUserClick,
                    contentDescription = "Profile picture of ${member.value?.name}"
                )

                SkeletonLoading(
                    value = member.value,
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
                        /*Text(
                            "${10512.toSuffixedString()} pts",
                            style = MaterialTheme.typography.labelSmall
                        )*/
                    }
                }
            }
        }
    }

}