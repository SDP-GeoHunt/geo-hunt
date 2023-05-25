package com.github.geohunt.app.ui.components.cards

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.R
import com.github.geohunt.app.i18n.DateFormatUtils
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.model.User
import com.github.geohunt.app.ui.components.buttons.ChallengeHuntState
import com.github.geohunt.app.ui.components.buttons.HuntClaimButton
import com.github.geohunt.app.ui.components.buttons.MenuButton
import com.github.geohunt.app.ui.components.buttons.MenuItem
import com.github.geohunt.app.ui.components.buttons.OpenMapButton
import com.github.geohunt.app.ui.components.utils.SkeletonLoading
import com.github.geohunt.app.ui.components.utils.SkeletonLoadingImage
import com.github.geohunt.app.ui.theme.geoHuntRed
import com.github.geohunt.app.utility.quantizeToLong
import java.time.LocalDateTime

val ChallengeCardContentPadding = 8.dp

/**
 * Draws the [ChallengeCardTitle] including the author's profile picture, name, and distance to the
 * challenge.
 *
 * @param author The author of the challenge, or null if loading.
 * @param distance The distance to the challenge, or null if loading.
 * @param publicationDate The publication date of the challenge.
 */
@Composable
private fun ChallengeCardTitle(
    author: User?,
    onUserClick: (User) -> Unit,
    distance: () -> Double?,
    publicationDate: LocalDateTime,
    isFollowing: Boolean,
    onFollow: (User, Boolean) -> Unit,
    canLeaveHunt: Boolean,
    onLeaveHunt: () -> Unit
) {
    return GeoHuntCardTitle(
        author = author,
        onUserClick = onUserClick,
        subtitle = {
            SkeletonLoading(value = distance(), width = 170.dp, height = 16.dp) { distance ->
                // Use derivedStateOf to recompose only when the UI would be changed
                // This avoids useless recompositions when publicationDate and/or distance slightly
                // change, but not enough to change the UI since we quantize them.
                val distanceAway = remember {
                    derivedStateOf {
                        when {
                            distance <= 0.5 -> "${distance.quantizeToLong(0.1) * 100}m away"
                            else -> "${distance.quantizeToLong(0.1) / 10.0}km away"
                        }
                    }
                }

                val elapsedTimeString = DateFormatUtils.getElapsedTimeString(
                    dateTime = publicationDate,
                    formattingStringId = R.string.published_format
                )
                val elapsedTime = remember { derivedStateOf { elapsedTimeString } }

                Text(
                    "${distanceAway.value} • ${elapsedTime.value}",
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        },
        action = {
            MenuButton {
                MenuItem(
                    title = if (!isFollowing) stringResource(R.string.follow) else stringResource(R.string.unfollow),
                    enabled = author != null,
                    onClick = {
                        if (author != null) {
                            onFollow(author, !isFollowing)
                        }
                    },
                    icon = {
                        Icon(
                            if (!isFollowing) Icons.Default.PersonAdd else Icons.Default.PersonRemove,
                            contentDescription = null
                        )
                    },
                    red = isFollowing
                )

                if (canLeaveHunt) {
                    MenuItem(
                        title = stringResource(R.string.leave_the_hunt),
                        onClick = onLeaveHunt,
                        icon = { Icon(Icons.Default.Logout, contentDescription = null) },
                        red = true
                    )
                }
            }
        }
    )
}

/**
 * Creates the challenge image in a [ChallengeCard].
 *
 * This Composable reacts to double taps to like.
 */
@Composable
fun ChallengeCardImage(
    url: String?,
    onClick: () -> Unit,
    onDoubleTap: () -> Unit
) {
    // Double-tap icon
    val iconSize = 48.dp
    var animationStarted by remember {
        mutableStateOf(false)
    }

    val animatedSize by animateDpAsState(
        targetValue = if (animationStarted) iconSize else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )

    BoxWithConstraints {
        SkeletonLoadingImage(
            url = url,
            width = maxWidth,
            height = 250.dp,
            modifier = Modifier
                .clip(CardDefaults.shape)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { if (url != null) onClick() },
                        onDoubleTap = {
                            if (url != null) {
                                animationStarted = true
                                onDoubleTap()
                            }
                        }
                    )
                },
            contentDescription = "Challenge image"
        )

        if (animationStarted) {
            Icon(
                painterResource(R.drawable.target_arrow),
                tint = geoHuntRed,
                modifier = Modifier
                    .size(animatedSize)
                    .align(Alignment.Center),
                contentDescription = null
            )

            if (animatedSize == iconSize) {
                animationStarted = false
            }
        }
    }
}

@Composable
fun ChallengeCardActions(
    huntState: ChallengeHuntState,
    onOpenMap: () -> Unit,
    onHunt: () -> Unit,
    onClaim: () -> Unit,
    isBusy: () -> Boolean
) {
    Row(
        Modifier.padding(ChallengeCardContentPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OpenMapButton(onClick = onOpenMap)

        Spacer(Modifier.weight(1.0f))

        HuntClaimButton(
            state = huntState,
            onHunt = onHunt,
            onClaim = onClaim,
            isBusy = isBusy
        )
    }
}

@Composable
fun ChallengeCard(
    challenge: Challenge,
    huntState: ChallengeHuntState,
    author: User?,
    onUserClick: (User) -> Unit,
    userLocation: () -> Location?,
    onImageClick: () -> Unit,
    onOpenMap: () -> Unit,
    isFollowing: Boolean,
    onFollow: (User, Boolean) -> Unit,
    onHunt: () -> Unit,
    onLeaveHunt: () -> Unit,
    onClaim: () -> Unit,
    isBusy: () -> Boolean
) {
    Card(Modifier.fillMaxWidth()) {
        ChallengeCardTitle(
            author = author,
            onUserClick = onUserClick,
            distance = { userLocation()?.distanceTo(challenge.location) },
            publicationDate = challenge.publishedDate,
            isFollowing = isFollowing,
            onFollow = onFollow,
            canLeaveHunt = huntState == ChallengeHuntState.HUNTED,
            onLeaveHunt = onLeaveHunt
        )

        ChallengeCardImage(
            url = challenge.photoUrl,
            onClick = onImageClick,
            onDoubleTap = { if (huntState == ChallengeHuntState.NOT_HUNTED) onHunt() }
        )

        ChallengeCardActions(
            huntState = huntState,
            onOpenMap = onOpenMap,
            onHunt = onHunt,
            onClaim = onClaim,
            isBusy = isBusy
        )
    }
}