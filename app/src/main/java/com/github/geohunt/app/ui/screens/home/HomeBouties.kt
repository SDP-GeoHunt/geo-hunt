package com.github.geohunt.app.ui.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.geohunt.app.R
import com.github.geohunt.app.model.Bounty
import com.github.geohunt.app.model.User
import kotlinx.coroutines.flow.map

@SuppressLint("FlowOperatorInvokedInComposition")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BountiesFeed(
    onUserClick: (User) -> Unit,
    showTeamProgress: (Bounty) -> Unit,
    showTeamChooser: (Bounty) -> Unit,
    viewModel: BountyFeedViewModel = viewModel(factory = BountyFeedViewModel.Factory)
) {
    val isRefreshing = viewModel.areBountiesRefreshing.collectAsStateWithLifecycle()
    val bounties by viewModel.bountyList.collectAsStateWithLifecycle()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing.value,
        onRefresh = { viewModel.refreshBounties() })
    val isInsideBounties by viewModel.isAlreadyInsideBounties.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        when {
            bounties == null -> CircularProgressIndicator(Modifier.align(Alignment.Center))
            bounties!!.isEmpty() -> {
                Text(
                    stringResource(id = R.string.no_bounties),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .testTag("no-bounties")
                )
            }
            else -> {
                LazyColumn {
                    items(bounties!!) { bounty ->
                        val isInside = isInsideBounties.map { it.bid }.contains(bounty.bid)
                        HomeBountyCard(
                            viewModel.bountyAuthors.map { it[bounty.bid] },
                            onUserClick = onUserClick,
                            bounty.name,
                            bounty.expirationDate,
                            viewModel.bountyChallenges.map { it[bounty.bid] },
                            viewModel.nbParticipating.map { it[bounty.bid] },
                            isInside
                        ) {
                            if (isInside) showTeamProgress(bounty)
                            else showTeamChooser(bounty)
                        }
                    }
                }
            }
        }

        PullRefreshIndicator(
            refreshing = isRefreshing.value,
            state = pullRefreshState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .testTag("loading-bounties")
        )
    }
}
