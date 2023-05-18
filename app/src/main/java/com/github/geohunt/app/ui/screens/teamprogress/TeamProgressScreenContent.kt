package com.github.geohunt.app.ui.screens.teamprogress

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.model.User
import com.github.geohunt.app.ui.components.bounties.BountyChallengeCard
import com.github.geohunt.app.ui.components.teamprogress.TeamProgressMembersCarousel
import com.github.geohunt.app.ui.components.teamprogress.TeamProgressTopAppBar
import com.github.geohunt.app.ui.utils.pagination.FinitePagedList
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TeamProgressScreenContent(
    onBack: () -> Unit,
    onLeaderboard: () -> Unit,
    onChat: () -> Unit,
    onHunt: (Challenge) -> Unit,

    teamName: String,
    teamMembers: FinitePagedList<User>,
    hunters: FinitePagedList<List<String>>,
    newMessages: StateFlow<Int>,
    locationState: StateFlow<Location?>,
    challenges: List<Challenge>?
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val currentLocation = locationState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { TeamProgressTopAppBar(
            // Try to limit team name to 20 characters, otherwise it is truncated in the app bar,
            // which is generally bad UX. The current work-around is to have ellipsis (...)
            // when the team name is too long.
            teamName = teamName,
            onBack = onBack,
            onLeaderboard = onLeaderboard,
            onChat = onChat,
            newMessagesState = newMessages,
            scrollBehavior = scrollBehavior
        ) }
    ) { padding ->
        LazyColumn(
            Modifier
                .padding(padding)
                .fillMaxWidth()) {
            item {
                Column(Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        "${teamMembers.size()} members",
                        style = MaterialTheme.typography.titleLarge
                    )

                    TeamProgressMembersCarousel(teamMembers = teamMembers)
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

                        /*Text(
                            "14/68 claimed",
                            style = MaterialTheme.typography.labelLarge
                        )*/
                    }
                }
            }

            if (challenges == null) {
                item {
                    Box(Modifier.fillMaxSize()) {
                        CircularProgressIndicator(Modifier.align(Alignment.Center).testTag("loadingChallenges"))
                    }
                }
            } else {
                itemsIndexed(challenges) { index, challenge ->
                    val challengeHunters = hunters.get(index).collectAsStateWithLifecycle()

                    BountyChallengeCard(
                        challenge = challenge,
                        numberOfHunters = challengeHunters.value?.size ?: 0,
                        currentLocation = currentLocation.value,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .fillMaxWidth(),
                        onHunt = onHunt
                    )
                }
            }
        }
    }

}