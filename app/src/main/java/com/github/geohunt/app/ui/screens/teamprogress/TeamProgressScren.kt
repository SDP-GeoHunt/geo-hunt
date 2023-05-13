package com.github.geohunt.app.ui.screens.teamprogress

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.geohunt.app.ui.components.teamprogress.TeamProgressMembersCarousel
import com.github.geohunt.app.ui.components.teamprogress.TeamProgressTopAppBar
import com.github.geohunt.app.ui.theme.GeoHuntTheme

/**
 * Creates the team progress screen.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TeamProgressScreen(
    onBack: () -> Unit,
    onLeaderboard: () -> Unit,
    onChat: () -> Unit,
    onInvite: () -> Unit,
    viewModel: TeamProgressViewModel = viewModel(factory = TeamProgressViewModel.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    GeoHuntTheme {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = { TeamProgressTopAppBar(
                // Try to limit team name to 20 characters, otherwise it is truncated in the app bar,
                // which is generally bad UX. The current work-around is to have ellipsis (...)
                // when the team name is too long.
                teamName = "Team name",
                onBack = onBack,
                onLeaderboard = onLeaderboard,
                onChat = onChat,
                onInvite = onInvite,
                newMessagesState = viewModel.newMessages,
                scrollBehavior = scrollBehavior
            )}
        ) { padding ->
            LazyColumn(
                Modifier
                    .padding(padding)
                    .fillMaxWidth()) {
                item {
                    Column(Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            "${viewModel.teamMembers.size} members",
                            style = MaterialTheme.typography.titleLarge
                        )

                        TeamProgressMembersCarousel(teamMembers = viewModel.teamMembers)
                    }
                }

                stickyHeader {
                    Surface {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Challenges",
                                style = MaterialTheme.typography.titleLarge
                            )

                            Spacer(Modifier.weight(1.0f))

                            Text(
                                "14/68 claimed",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }

                items(100) {
                    Text("text $it")
                }
            }
        }
    }
}
