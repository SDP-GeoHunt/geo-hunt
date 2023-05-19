package com.github.geohunt.app.ui.components.teamprogress

import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow

/**
 * The maximum number displayed in a badge.
 */
private const val MAX_BADGE_NUMBER = 999

/**
 * Creates the team progress top app bar.
 *
 * @param teamName The team name displayed as the title of the top bar.
 * @param onBack The callback used when pressing the back arrow.
 * @param onLeaderboard The callback used when pressing the leaderboard button.
 * @param newMessagesState The number of new messages of the chat.
 * @param scrollBehavior The scroll behavior of the app bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamProgressTopAppBar(
    teamName: String,
    onBack: () -> Unit,
    onLeaderboard: () -> Unit,
    onChat: () -> Unit,
    newMessagesState: StateFlow<Int>,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val newMessages = newMessagesState.collectAsStateWithLifecycle()

    MediumTopAppBar(
        title = { Text(teamName, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back button"
                )
            }
        },
        actions = {
            IconButton(
                onClick = onLeaderboard,
                modifier = Modifier.testTag("Open leaderboard")
            ) {
                Icon(
                    Icons.Outlined.Leaderboard,
                    contentDescription = "Open leaderboard"
                )
            }

            IconButton(
                onClick = onChat,
                modifier = Modifier.testTag("Open chat")
            ) {
                BadgedBox(badge = {
                    if (newMessages.value > 0) {
                        val badgeContent = when {
                            newMessages.value > MAX_BADGE_NUMBER -> "$MAX_BADGE_NUMBER+"
                            else -> newMessages.value.toString()
                        }

                        Badge {
                            Text(
                                badgeContent,
                                modifier = Modifier.semantics {
                                    contentDescription = "$badgeContent new messages"
                                }
                            )
                        }
                    }
                }) {
                    Icon(
                        Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Open chat"
                    )
                }
            }
        }
    )
}
