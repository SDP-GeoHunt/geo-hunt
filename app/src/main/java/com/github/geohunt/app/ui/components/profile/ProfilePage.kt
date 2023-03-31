package com.github.geohunt.app.ui.components.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.sharp.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.R
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.model.database.firebase.FirebaseDatabase
import com.github.geohunt.app.model.database.firebase.FirebaseUserRef
import com.github.geohunt.app.ui.components.button.FlatLongButton
import com.github.geohunt.app.ui.components.user.ProfileIcon
import com.github.geohunt.app.ui.rememberLazyRef
import com.github.geohunt.app.utility.findActivity
import kotlinx.coroutines.async

typealias OptionalCallback = (() -> Any)?

/**
 * The main profile page content.
 *
 * @param id The id of the user to be shown
 * @param openProfileEdit A callback triggered when the user wants to edit his profile
 * @param onLogout A callback triggered when the user wants to sign out
 */
@Composable
fun ProfilePage(id: String, openProfileEdit: OptionalCallback = null, onLogout: OptionalCallback = null) {
    ProfilePage(
        user = FirebaseUserRef(id, FirebaseDatabase(LocalContext.current.findActivity())),
        openProfileEdit = openProfileEdit,
        onLogout = onLogout
    )
}

/**
 * The main profile page content.
 *
 * @param user A lazy ref for the user
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProfilePage(user: LazyRef<User>, openProfileEdit: OptionalCallback = null, onLogout: OptionalCallback = null) {
    val lazyRefRemember = rememberLazyRef { user }
    val drawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val isMoreOptionsAvailable = openProfileEdit != null || onLogout != null

    Box(modifier = Modifier
        .fillMaxSize()) {
        if (lazyRefRemember.value == null) {
            Progress()
        } else {
            BottomDrawer(
                drawerState = drawerState,
                drawerContent = {
                    if (isMoreOptionsAvailable)
                        SettingsDrawerContent(openProfileEdit, onLogout, {
                            coroutineScope.async { drawerState.close() }
                        })
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
                        IconButton(onClick = { onSettingsClick() }) {
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

@Composable
private fun SettingsDrawerContent(openProfileEdit: OptionalCallback, onLogout: OptionalCallback, close: () -> Any) {
    var isSureToLogOff by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (openProfileEdit != null) {
            FlatLongButton(
                icon = Icons.Default.Edit,
                text = stringResource(id = R.string.edit_profile),
                onClick = { close(); openProfileEdit() },
            )
        }

        if (onLogout != null) {
            FlatLongButton(
                icon = Icons.Default.Logout,
                text = stringResource(if (isSureToLogOff) R.string.log_out_confirmation else R.string.log_out),
                textColor = MaterialTheme.colors.error,
                onClick = {
                    if (isSureToLogOff) onLogout() else isSureToLogOff = true
                }
            )
        }
    }
}

private data class BigNumberContent(val title: String, val subtitleId: Int)
@Composable
private fun UserNumberDetails(user: User) {
    val numbers = listOf(
        BigNumberContent(user.challenges.size.toString(), R.string.profile_number_of_posts_subtitle),
        BigNumberContent(user.hunts.size.toString(), R.string.profile_number_of_hunts_subtitle),
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