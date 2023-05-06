package com.github.geohunt.app.ui.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.R
import com.github.geohunt.app.model.Bounty
import com.github.geohunt.app.ui.components.navigation.HiddenRoute
import com.google.accompanist.pager.HorizontalPagerIndicator
import kotlinx.coroutines.flow.Flow
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
                    null -> CircularProgressIndicator(
                        modifier = fm
                            .align(Alignment.Center)
                            .height(48.dp)
                    )
                    listOf<Bounty>() -> {
                        Text(
                            stringResource(id = R.string.no_bounties),
                            textAlign = TextAlign.Center,
                            modifier = fm
                        )
                    }
                    else -> {
                        LazyColumn {
                            items(bounties!!) { bounty ->
                                BountyCard(
                                    vm.bountyChallenges.map { it[bounty.bid] },
                                    vm.nbParticipating.map { it[bounty.bid] }
                                ) {
                                    navigate("${HiddenRoute.ViewBounty.route}/${bounty.bid}")
                                }
                            }
                        }
                    }
                }

                PullRefreshIndicator(
                    refreshing = isRefreshing.value,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BountyCard(
    challengesFlows: Flow<List<Challenge>?>,
    members: Flow<Int?>,
    join: () -> Any
) {

    val challenges by challengesFlows.collectAsState(initial = null)
    val nbMembers by members.collectAsState(initial = null)

    Card(modifier = Modifier.padding(16.dp), elevation = 4.dp) {
        Column {

            val pagerStatus = challengesImageSlider(challenges)

            Divider()

            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Number of challenges
                Icon(
                    painterResource(
                        id = R.drawable.target_arrow
                    ), null
                )
                Text((challenges?.size ?: "…").toString())

                InlineSeparator()

                Icon(Icons.Filled.Person, null)
                Text((nbMembers ?: "…").toString())

                pagerStatus?.let {
                    val (nb_elements, state) = it

                    Spacer(Modifier.weight(1f))
                    HorizontalPagerIndicator(state, nb_elements)
                }

                Spacer(Modifier.weight(1f))
                TextButton(onClick = { join() }) {
                    Text(stringResource(id = R.string.join))
                }
            }
        }
    }
}

@Composable
private fun InlineSeparator() {
    Text("⋅", modifier = Modifier.padding(horizontal = 8.dp))
}


/**
 * This function creates a slider and returns the tuple (nb_elements, pager_state)
 * used to create a dot indicator.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun challengesImageSlider(challenges: List<Challenge>?): Pair<Int, PagerState>? {
    if (challenges == null) return null
    if (challenges.isEmpty()) return null

    // The aspect ratio is given by the first
    val firstImagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(challenges[0].photoUrl)
            .size(Size.ORIGINAL)
            .build()
    )
    val pagerState = rememberPagerState()

    Column {
        HorizontalPager(pageCount = challenges.size, state = pagerState) {
            val dimensionModifier = (firstImagePainter.state as? AsyncImagePainter.State.Success)
                ?.painter?.intrinsicSize?.let { size ->
                    Modifier
                        .aspectRatio(size.width/size.height)
                } ?: Modifier


            if (it == 0) {
                Image(
                    painter = firstImagePainter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else
                AsyncImage(
                    challenges[it].photoUrl,
                    contentDescription = null,
                    modifier = dimensionModifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
        }
    }

    return Pair(challenges.size, pagerState)
}