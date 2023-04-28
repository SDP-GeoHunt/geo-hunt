package com.github.geohunt.app.ui.components.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.R
import com.github.geohunt.app.model.LiveLazyRef
import com.github.geohunt.app.model.database.Database
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.ui.components.user.ProfileIcon
import com.github.geohunt.app.ui.rememberLiveLazyRef
import kotlinx.coroutines.async

typealias OptionalCallback = (() -> Any)?


/**
 * The main profile page content.
 *
 * @param profilePageViewModel The profile page's view model.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProfilePage(
    profilePageViewModel: ProfilePageViewModel,
    openProfileEdit: OptionalCallback = null,
    openLeaderboard: OptionalCallback = null,
    onLogout: OptionalCallback = null
) {
    val drawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val isMoreOptionsAvailable = openProfileEdit != null || onLogout != null || openLeaderboard != null

    val user = profilePageViewModel.user.collectAsState()


    Box(modifier = Modifier
        .fillMaxSize()) {
        if (user.value == null) {
            Progress()
        } else {
            BottomDrawer(
                drawerState = drawerState,
                drawerContent = {
                    if (isMoreOptionsAvailable)
                        SettingsDrawer(openProfileEdit, openLeaderboard, onLogout) {
                            coroutineScope.async { drawerState.close() }
                        }
                }
            ) {
                ProfilePageContent(profilePageViewModel, isMoreOptionsAvailable) {
                    // On settings click
                    coroutineScope.async { drawerState.open() }
                }
            }
        }
    }
}

@Composable
@NoLiveLiterals // Without this, tests do not passes (issue with Jetpack Compose)
private fun ProfilePageContent(vm: ProfilePageViewModel, showSettingsBtn: Boolean = false, onSettingsClick: () -> Any) {
    val userState = vm.user.collectAsState()
    val user = userState.value ?: return

    Column {
        Row {
            ProfileIcon(user = user, modifier = Modifier
                .width(124.dp)
                .aspectRatio(1f))

            Column(modifier = Modifier.padding(0.dp, 12.dp)) {
                Row {
                    Text(user.displayName ?: user.id)

                    Spacer(Modifier.weight(1f))

                    if (showSettingsBtn) {
                        IconButton(onClick = { onSettingsClick() }, modifier = Modifier.testTag("profile-settings-btn")) {
                            Icon(
                                Icons.Sharp.Settings,
                                contentDescription = stringResource(id = R.string.settings)
                            )
                        }
                    }
                }

                UserNumberDetails(vm)
            }
        }

        PastChallengeAndHunts(vm)
    }
}

private data class BigNumberContent(val title: String, val subtitleId: Int)
@Composable
private fun UserNumberDetails(vm: ProfilePageViewModel) {
    val score = vm.score.collectAsState(initial = null)
    val hunts = vm.claims.collectAsState()
    val challenges = vm.challenges.collectAsState()

    val numbers = listOf(
        BigNumberContent(if (challenges.value == null) "…" else challenges.value?.size.toString(), R.string.profile_number_of_posts_subtitle),
        BigNumberContent(if (hunts.value == null) "…" else hunts.value?.size.toString(), R.string.profile_number_of_hunts_subtitle),
        BigNumberContent(if (score.value == null) "…" else score.value.toString(), R.string.profile_score_subtitle),
    )

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {

        for(n in numbers) {
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