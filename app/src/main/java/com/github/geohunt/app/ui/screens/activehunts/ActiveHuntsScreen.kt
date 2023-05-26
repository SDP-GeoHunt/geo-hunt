package com.github.geohunt.app.ui.screens.activehunts

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.geohunt.app.R
import com.github.geohunt.app.model.Bounty
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.ui.components.GeoHuntTabs
import com.github.geohunt.app.ui.components.TabData
import com.github.geohunt.app.ui.components.activehunts.ActiveBountiesList
import com.github.geohunt.app.ui.components.activehunts.ActiveHuntsList
import com.github.geohunt.app.ui.components.activehunts.ActiveHuntsTitle
import kotlinx.coroutines.flow.StateFlow

enum class ActiveHuntsTabs {
    Challenges,
    Bounties
}

/**
 * A screen that shows all the active hunts of a user.
 *
 * The hunts are displayed on a horizontal scrollable list.
 *
 * @param openExploreTab The function called to open the explore view in the navigation.
 * @param viewModel The [ActiveHuntsViewModel] related to this screen.
 *
 * @see [ActiveHuntsList]
 */
@Composable
fun ActiveHuntsScreen(
    openExploreTab: () -> Unit,
    openChallengeView: (Challenge) -> Unit,
    openBountyView: (Bounty) -> Unit,
    viewModel: ActiveHuntsViewModel = viewModel(factory = ActiveHuntsViewModel.Factory)
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        ActiveHuntsTitle()

        val challenges = viewModel.activeHunts.collectAsStateWithLifecycle()
        val bounties = viewModel.activeBounties.collectAsStateWithLifecycle()

        var currentTab by remember { mutableStateOf(ActiveHuntsTabs.Challenges) }

        GeoHuntTabs(tabs = listOf(
            TabData(title = stringResource(id = R.string.challenges), onClick = { currentTab = ActiveHuntsTabs.Challenges }),
            TabData(title = stringResource(id = R.string.bounties), onClick = { currentTab = ActiveHuntsTabs.Bounties }),
        ))

        Spacer(modifier = Modifier.size(10.dp))

        when (currentTab) {
            ActiveHuntsTabs.Challenges -> ActiveChallenges(
                challenges.value,
                openExploreTab,
                openChallengeView,
                viewModel::getAuthorName
            )

            ActiveHuntsTabs.Bounties -> ActiveBounties(
                bounties.value,
                openExploreTab,
                openBountyView
            )
        }
    }
}

@Composable
fun ActiveChallenges(
        challenges: List<Challenge>?,
        openExploreTab: () -> Unit,
        openChallengeView: (Challenge) -> Unit,
        getAuthorName: (Challenge) -> StateFlow<String>) {
    when (challenges) {
        null -> Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        else -> ActiveHuntsList(
                challenges,
                openExploreTab,
                openChallengeView,
                getAuthorName)
    }
}

@Composable
fun ActiveBounties(
        bounties: List<Pair<Bounty, Challenge>>?,
        openExploreTab: () -> Unit,
        openBountyView: (Bounty) -> Unit
) {
    when (bounties) {
        null -> Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        else -> ActiveBountiesList(
            bounties,
            openExploreTab,
            openBountyView
        )
    }
}
