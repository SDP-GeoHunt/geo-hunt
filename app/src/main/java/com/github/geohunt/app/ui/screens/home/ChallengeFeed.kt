package com.github.geohunt.app.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.User
import com.github.geohunt.app.ui.components.appbar.HomeScreenFeed
import com.github.geohunt.app.ui.components.cards.ChallengeCard

@Composable
fun ChallengeFeed(
    chosenFeed: HomeScreenFeed,
    onUserClick: (User) -> Unit,
    onOpenMap: (Challenge) -> Unit,
    onOpenChallenge: (Challenge) -> Unit,
    onClaim: (Challenge) -> Unit,
    onOpenExplore: () -> Unit,
    viewModel: HomeFeedViewModel = viewModel(factory = HomeFeedViewModel.Factory)
) {
    val feed = viewModel.getFeed(chosenFeed)
    val challenges = feed.challenges.collectAsStateWithLifecycle()

    when {
        challenges.value == null -> Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        challenges.value!!.isEmpty() -> Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "This place is a bit empty.",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                "You can check for nearby challenges on the map !",
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(Modifier.height(12.dp))

            Button(onClick = onOpenExplore, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Open the map")
            }
        }

        else -> LazyColumn(
            Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Additional null check to avoid NPE
            if (challenges.value != null) {
                items(
                    challenges.value!!,
                    key = { it.id }
                ) { challenge ->
                    val huntState = feed.getChallengeHuntState(challenge).collectAsStateWithLifecycle()
                    val author = feed.getAuthor(challenge).collectAsStateWithLifecycle()
                    val isFollowing = viewModel.isFollowing(author.value).collectAsStateWithLifecycle(initialValue = false)
                    val isBusy = feed.isBusy(challenge).collectAsStateWithLifecycle()

                    ChallengeCard(
                        challenge = challenge,
                        huntState = huntState.value,
                        author = author.value,
                        onUserClick = onUserClick,
                        userLocation = { viewModel.userLocation.value },
                        onImageClick = { onOpenChallenge(challenge) },
                        onOpenMap = { onOpenMap(challenge) },
                        isFollowing = isFollowing.value,
                        onFollow = { user, follow -> if (follow) viewModel.follow(user) else viewModel.unfollow(user) },
                        onHunt = { feed.hunt(challenge) } ,
                        onLeaveHunt = { feed.leaveHunt(challenge) },
                        onClaim = { onClaim(challenge) },
                        isBusy = { isBusy.value }
                    )
                }

                // Whitespace
                item {
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}
