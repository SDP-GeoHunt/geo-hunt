package com.github.geohunt.app.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.geohunt.app.model.Bounty
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.User
import com.github.geohunt.app.ui.components.appbar.FeedSelectionDrawer
import com.github.geohunt.app.ui.components.appbar.HomeScreenFeed
import com.github.geohunt.app.ui.components.appbar.MainAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onUserClick: (User) -> Unit,
    onOpenMap: (Challenge) -> Unit,
    onOpenChallenge: (Challenge) -> Unit,
    onClaim: (Challenge) -> Unit,
    onOpenExplore: () -> Unit,
    showTeamProgress: (Bounty) -> Unit,
    showTeamChooser: (Bounty) -> Unit
) {
    FeedSelectionDrawer { feed, openDrawer ->
        Scaffold(
            topBar = { MainAppBar(title = feed.title(), openDrawer) }
        ) { padding ->
            Box(Modifier.padding(padding)) {
                when(feed) {
                    HomeScreenFeed.Home,
                    HomeScreenFeed.Discover -> ChallengeFeed(
                        chosenFeed = feed,
                        onUserClick = onUserClick,
                        onOpenMap = onOpenMap,
                        onOpenChallenge = onOpenChallenge,
                        onClaim = onClaim,
                        onOpenExplore = onOpenExplore
                    )

                    HomeScreenFeed.Bounties -> BountiesFeed(
                        onUserClick = onUserClick,
                        showTeamProgress = showTeamProgress,
                        showTeamChooser = showTeamChooser
                    )
                }
            }
        }
    }
}
