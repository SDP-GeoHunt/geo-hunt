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
    onLogout: OptionalCallback = null
) {
    val drawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val isMoreOptionsAvailable =
        openProfileEdit != null || onLogout != null || openLeaderboard != null

    // Refreshing
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.refresh() })

    val user = viewModel.user.collectAsState()
    val challenges = viewModel.challenges.collectAsState()
    val claims = viewModel.claims.collectAsState()
    val hunts = viewModel.claimedChallenges.collectAsState()
    val score = claims.value?.sumOf { it.awardedPoints }
    val error = viewModel.didFail.collectAsState()

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

            BottomDrawer(
                gesturesEnabled = drawerState.isOpen,
                drawerState = drawerState,
                drawerContent = {
                    if (isMoreOptionsAvailable)
                        SettingsDrawer(openProfileEdit, openLeaderboard, onLogout) {
                            coroutineScope.async { drawerState.close() }
                        }
                },
                modifier = Modifier.height(this@BoxWithConstraints.maxHeight)
            ) {
                if (error.value != null) {
                    Text(text = "User does not exist.")
                } else if (user.value == null) {
                    Progress()
                } else {
                    ProfilePageContent(
                        user.value!!,
                        challenges.value,
                        hunts.value,
                        score,
                        if (viewModel.isSelf) { { coroutineScope.async {
                            drawerState.open()
                        } } } else null
                    )
                }
            }
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
    onSettingsClick: (() -> Any)?
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
            }
        }

        PastChallengeAndHunts(challenges, hunts)
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