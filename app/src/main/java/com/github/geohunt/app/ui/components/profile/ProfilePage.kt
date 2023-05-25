package com.github.geohunt.app.ui.components.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Settings
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.geohunt.app.R
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.User
import com.github.geohunt.app.ui.components.user.ProfileIcon
import kotlinx.coroutines.async

typealias OptionalCallback = (() -> Any)?


/**
 * The main profile page content.
 *
 * @param viewModel The profile page's view model.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProfilePage(
    viewModel: ProfilePageViewModel = viewModel(factory = ProfilePageViewModel.Factory),
    openProfileEdit: OptionalCallback = null,
    openLeaderboard: OptionalCallback = null,
    onLogout: OptionalCallback = null,
    openSettings: OptionalCallback = null,
    openChallengeView: (Challenge) -> Unit = { },
) {
    val drawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val isMoreOptionsAvailable =
        openProfileEdit != null || onLogout != null || openLeaderboard != null || openSettings != null

    // Refreshing
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.refresh() })

    val isPrivate = viewModel.isPrivate.collectAsState()
    val user = viewModel.user.collectAsState()
    val challenges = viewModel.challenges.collectAsState()
    val hunts = viewModel.claimedChallenges.collectAsState()
    val score = viewModel.score.collectAsState()
    val error = viewModel.didFail.collectAsState()
    val doesFollow = viewModel.doesFollow.collectAsState()

    BoxWithConstraints {
        Box(
            modifier = Modifier
                    .pullRefresh(pullRefreshState)
                    .verticalScroll(
                            rememberScrollState()
                    )
        ) {
            PullRefreshIndicator(
                refreshing = isRefreshing, state = pullRefreshState, modifier = Modifier.align(
                    Alignment.TopCenter
                )
            )

            val fullModifier = Modifier
                    .height(this@BoxWithConstraints.maxHeight)
                    .width(this@BoxWithConstraints.maxWidth)

            SetupDrawer(
                enabled = isMoreOptionsAvailable,
                drawerState = drawerState,
                drawerContent = {
                    SettingsDrawer(
                        openProfileEdit,
                        openLeaderboard,
                        onLogout,
                        openSettings
                    ) {
                        coroutineScope.async { drawerState.close() }
                    }
                },
                modifier = fullModifier
            ) {
                if (error.value != null) {
                    ErrorFetchingPage(
                        stringResource(id = R.string.error_fetching_page),
                        fullModifier.testTag("error-profile")
                    )
                } else if (isPrivate.value) {
                    ErrorFetchingPage(
                        stringResource(id = R.string.private_profile_page),
                        fullModifier.testTag("private-profile")
                    )
                } else {
                    if (user.value == null) {
                        Progress()
                    } else {
                        ProfilePageContent(
                            user.value!!,
                            challenges.value,
                            hunts.value,
                            score.value,
                            // Do not show settings if seeing another profile
                            if (viewModel.isSelf) {
                                {
                                    coroutineScope.async {
                                        drawerState.open()
                                    }
                                }
                            } else null,
                            if (viewModel.canFollow) {
                                doesFollow.value
                            } else null,
                                openChallengeView
                        ) {
                            if (viewModel.canFollow) {
                                viewModel.toggleFollow()
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SetupDrawer(
    enabled: Boolean,
    drawerState: BottomDrawerState,
    drawerContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {
    if (enabled) {
        BottomDrawer(
            gesturesEnabled = drawerState.isOpen,
            drawerState = drawerState,
            drawerContent = { drawerContent() },
            modifier = modifier
        ) {
            content()
        }
    } else {
        Column(modifier) {
            content()
        }
    }
}

@Composable
@NoLiveLiterals // Without this, tests do not passes (issue with Jetpack Compose)
fun ProfilePageContent(
    user: User,
    challenges: List<Challenge>?,
    hunts: List<Challenge>?,
    score: Long?,
    onSettingsClick: (() -> Any)?,
    isFollowed: Boolean? = null,
    openChallengeView: (Challenge) -> Unit = { },
    toggleFollow: (() -> Any)? = null
) {
    Column {
        Row {
            ProfileIcon(
                user = user, modifier = Modifier
                    .width(124.dp)
                    .aspectRatio(1f)
            )

            Column(modifier = Modifier.padding(0.dp, 12.dp)) {
                Row {
                    Text(user.displayName ?: user.id)

                    Spacer(Modifier.weight(1f))

                    onSettingsClick?.let { onSettingsClick ->
                        IconButton(
                            onClick = { onSettingsClick() },
                            modifier = Modifier.testTag("profile-settings-btn")
                        ) {
                            Icon(
                                Icons.Sharp.Settings,
                                contentDescription = stringResource(id = R.string.settings)
                            )
                        }
                    }
                }

                UserNumberDetails(challenges?.size, hunts?.size, score)

                if (toggleFollow != null && isFollowed != null) {
                    Button(
                        onClick = { toggleFollow() },
                        modifier = Modifier.testTag("follow-btn")
                    ) {
                        Text(
                            if (isFollowed) stringResource(id = R.string.followed) else stringResource(
                                id = R.string.follow
                            )
                        )
                    }
                }

            }
        }

        PastChallengeAndHunts(challenges, hunts, openChallengeView)
    }
}

private data class BigNumberContent(val title: String, val subtitleId: Int)

@Composable
private fun UserNumberDetails(
    challengesNumber: Int?,
    huntsNumber: Int?,
    score: Long?
) {
    val numbers = listOf(
        BigNumberContent(
            challengesNumber?.toString() ?: "…",
            R.string.profile_number_of_posts_subtitle
        ),
        BigNumberContent(huntsNumber?.toString() ?: "…", R.string.profile_number_of_hunts_subtitle),
        BigNumberContent(score?.toString() ?: "…", R.string.profile_score_subtitle),
    )

    Row(
        modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        for (n in numbers) {
            BigNumberWithText(title = n.title, subtitle = stringResource(n.subtitleId))
        }

    }
}

@Composable
private fun BigNumberWithText(title: String, subtitle: String) {
    Column {
        Text(title, textAlign = TextAlign.Center, style = MaterialTheme.typography.h1)
        Text(subtitle, textAlign = TextAlign.Center, style = MaterialTheme.typography.h3)
    }
}

@Composable
@NoLiveLiterals // Without this, tests do not passes (issue with Jetpack Compose)
private fun Progress() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(modifier = Modifier.testTag("progress"))
    }
}

@Composable
private fun ErrorFetchingPage(string: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = string,
            textAlign = TextAlign.Center
        )
    }
}