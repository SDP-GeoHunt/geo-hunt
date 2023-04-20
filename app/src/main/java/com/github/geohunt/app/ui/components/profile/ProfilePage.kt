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
import com.github.geohunt.app.model.database.api.Database
import com.github.geohunt.app.model.database.api.LoggedUserContext
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.ui.components.user.ProfileIcon
import com.github.geohunt.app.ui.rememberLiveLazyRef
import kotlinx.coroutines.async

typealias OptionalCallback = (() -> Any)?

@Composable
fun LoggedUserContext.ProfilePage(
    openProfileEdit: OptionalCallback = null,
    onLogout: OptionalCallback = null,
    openLeaderboard: OptionalCallback = null
) {
    ProfilePage(
        loggedUserRef,
        openProfileEdit = openProfileEdit,
        onLogout = onLogout,
        openLeaderboard = openLeaderboard
    )
}

/**
 * The main profile page content.
 *
 * @param id The id of the user to be shown
 * @param openProfileEdit A callback triggered when the user wants to edit his profile
 * @param onLogout A callback triggered when the user wants to sign out
 */
@Composable
fun ProfilePage(
    id: String,
    database: Database,
    openProfileEdit: OptionalCallback = null,
    onLogout: OptionalCallback = null,
    openLeaderboard: OptionalCallback = null
) {
    ProfilePage(
        user = database.getUserById(id),
        openProfileEdit = openProfileEdit,
        onLogout = onLogout,
        openLeaderboard = openLeaderboard
    )
}

/**
 * The main profile page content.
 *
 * @param user A lazy ref for the user
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProfilePage(
    user: LiveLazyRef<User>,
    openProfileEdit: OptionalCallback = null,
    openLeaderboard: OptionalCallback = null,
    onLogout: OptionalCallback = null
) {
    val lazyRefRemember = rememberLiveLazyRef { user }
    val drawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val isMoreOptionsAvailable = openProfileEdit != null || onLogout != null || openLeaderboard != null


    Box(modifier = Modifier
        .fillMaxSize()) {
        if (lazyRefRemember.value == null) {
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
                ProfilePageContent(lazyRefRemember.value!!, isMoreOptionsAvailable) {
                    // On settings click
                    coroutineScope.async { drawerState.open() }
                }
            }
        }
    }
}

@Composable
@NoLiveLiterals // Without this, tests do not passes (issue with Jetpack Compose)
private fun ProfilePageContent(user: User, showSettingsBtn: Boolean = false, onSettingsClick: () -> Any) {

    Column {
        Row {
            ProfileIcon(user = user, modifier = Modifier
                .width(124.dp)
                .aspectRatio(1f))

            Column(modifier = Modifier.padding(0.dp, 12.dp)) {
                Row {
                    Text(user.displayName ?: user.uid)

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

                UserNumberDetails(user)
            }
        }

        PastChallengeAndHunts(user)
    }
}

private data class BigNumberContent(val title: String, val subtitleId: Int)
@Composable
private fun UserNumberDetails(user: User) {
    val numbers = listOf(
        BigNumberContent(user.challenges.size.toString(), R.string.profile_number_of_posts_subtitle),
        BigNumberContent(user.activeHunts.size.toString(), R.string.profile_number_of_hunts_subtitle),
        BigNumberContent(user.score.toString(), R.string.profile_score_subtitle),
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