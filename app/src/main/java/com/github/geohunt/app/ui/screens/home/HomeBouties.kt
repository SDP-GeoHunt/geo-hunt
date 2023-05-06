package com.github.geohunt.app.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.R
import com.github.geohunt.app.ui.components.navigation.HiddenRoute
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeBounties(vm: HomeViewModel, navigate: (String) -> Any) {
    val isRefreshing = vm.areBountiesRefreshing.collectAsState()
    val bounties by vm.bountyList.collectAsState()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing.value,
        onRefresh = { vm.refreshBounties() })

    Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
        when (bounties) {
            null -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            else -> {
                LazyColumn {
                    items(bounties!!) { bounty ->
                        BountyCard(vm.bountyChallenges[bounty.bid], vm.nbParticipating[bounty.bid]) {
                            navigate("${HiddenRoute.ViewBounty.route}/${bounty.bid}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BountyCard(
    challengesFlows: StateFlow<List<Challenge>?>?,
    members: StateFlow<Int?>?,
    join: () -> Any
) {
    if (challengesFlows == null || members == null) return

    val challenges by challengesFlows.collectAsState()
    val nbMembers by members.collectAsState()

    Card {
        ChallengesImageSlider(challenges)
        Divider()
        Row {

            // Number of challenges
            Icon(
                painterResource(
                    id = R.drawable.target_arrow
                ), null
            )
            Text("⋅")
            Text((challenges?.size ?: "…").toString())
            Spacer(Modifier.width(4.dp))

            Icon(Icons.Filled.Person, null)
            Text("⋅")
            Text((nbMembers ?: "…").toString())

            Spacer(Modifier.weight(1f))
            TextButton(onClick = { join() }) {
                Text(stringResource(id = R.string.join))
            }
        }
    }
}

@Composable
private fun ChallengesImageSlider(challenge: List<Challenge>?) {
    if (challenge == null) return
    /*HorizontalPager(pageCount = challenge.size, key = { challenge[it]}) {

    }*/

}