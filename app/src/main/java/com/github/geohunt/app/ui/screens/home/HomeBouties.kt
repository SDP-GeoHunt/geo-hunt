package com.github.geohunt.app.ui.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.github.geohunt.app.R
import com.github.geohunt.app.model.Bounty
import com.github.geohunt.app.ui.components.navigation.HiddenRoute
import kotlinx.coroutines.flow.map

@SuppressLint("FlowOperatorInvokedInComposition")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeBounties(vm: HomeViewModel, navigate: (String) -> Any) {
    val isRefreshing = vm.areBountiesRefreshing.collectAsState()
    val bounties by vm.bountyList.collectAsState()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing.value,
        onRefresh = { vm.refreshBounties() })
    val isInsideBounties by vm.isAlreadyInsideBounties.collectAsState()

    BoxWithConstraints {
        Box(
            modifier = Modifier
                .pullRefresh(pullRefreshState)
                .verticalScroll(rememberScrollState())
        ) {
            val fm = Modifier
                .width(this@BoxWithConstraints.maxWidth)
                .height(this@BoxWithConstraints.maxHeight)

            Box(modifier = fm) {
                when (bounties) {
                    null -> { }
                    listOf<Bounty>() -> {
                        Text(
                            stringResource(id = R.string.no_bounties),
                            textAlign = TextAlign.Center,
                            modifier = fm.testTag("no-bounties")
                        )
                    }
                    else -> {
                        LazyColumn {
                            items(bounties!!) { bounty ->
                                val isInside = isInsideBounties.map { it.bid }.contains(bounty.bid)
                                HomeBountyCard(
                                    vm.bountyAuthors.map { it[bounty.bid] },
                                    bounty.name,
                                    bounty.expirationDate,
                                    vm.bountyChallenges.map { it[bounty.bid] },
                                    vm.nbParticipating.map { it[bounty.bid] },
                                    isInside
                                ) {
                                    if (isInside) navigate("${HiddenRoute.BountyView.route}/${bounty.bid}")
                                    else navigate("${HiddenRoute.BountyTeamChooser.route}/${bounty.bid}")
                                }
                            }
                        }
                    }
                }

                PullRefreshIndicator(
                    refreshing = isRefreshing.value,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter).testTag("loading-bounties")
                )
            }
        }
    }

}
