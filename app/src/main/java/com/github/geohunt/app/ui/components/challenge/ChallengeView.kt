package com.github.geohunt.app.ui.components.challenge

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.github.geohunt.app.R
import com.github.geohunt.app.ui.components.GoBackBtn
import com.github.geohunt.app.ui.components.utils.AwaitNullable
import com.github.geohunt.app.ui.screens.home.HorizontalDivider

/**
 * @brief Composable function that displays a specific challenge including details
 * such as previous claims and the author to the user.
 *
 * @param viewModel The view model
 */
@Composable
fun ChallengeView(
    cid: String,
    fnViewImageCallback: (String) -> Unit,
    fnClaimHuntCallback: (String) -> Unit,
    fnGoBackBtn: () -> Unit,
    viewModel: ChallengeViewModel = viewModel(factory = ChallengeViewModel.Factory)
) {
    val lazyState = rememberLazyListState()
    val transition = updateTransition(
        remember { derivedStateOf { lazyState.firstVisibleItemIndex != 0 } },
        label = "Image size transition"
    )
    val imageAspectRatio by transition.animateFloat(label = "Animate image size ratio") { isScrolling ->
        if (isScrolling.value) 1.8f else 1.0f
    }

    val nullableState = viewModel.state.collectAsState()
    LaunchedEffect(viewModel, cid) {
        viewModel.withChallengeId(cid)
    }

    AwaitNullable(state = nullableState) { state ->
        Box {
            Column {
                // Main challenge view image
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(state.challenge.photoUrl)
                        .crossfade(true)
                        .build(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            fnViewImageCallback(state.challenge.photoUrl)
                        }
                        .aspectRatio(imageAspectRatio, false)
                        .testTag("challenge-main-image"),
                    contentDescription = "Challenge Image"
                )

                // Button bar bellow the image
                BelowImageButton(
                    joinHunt = { viewModel.joinHunt() },
                    leaveHunt = { viewModel.leaveHunt() },
                    state = state,
                    fnClaimHuntCallback = fnClaimHuntCallback)

                // Spacer and horizontal divider
                Spacer(Modifier.height(2.dp))

                HorizontalDivider(padding = 2.dp)

                // Finally lazy list of claim list
                LazyClaimList(lazyState, viewModel, state, fnViewImageCallback)
            }

            GoBackBtn(fnGoBackBtn)
        }
    }
}

@Composable
private fun LazyClaimList(
    lazyState: LazyListState,
    viewModel: ChallengeViewModel,
    state: ChallengeViewModel.State,
    fnViewImageCallback: (String) -> Unit
) {
    LazyColumn(
        state = lazyState,
        modifier = Modifier.fillMaxHeight()
    ) {
        item {
            Spacer(modifier = Modifier.height(1.dp))
        }

        item {
            ChallengeViewAuthor(
                doFollow = viewModel::follow,
                doUnfollow = viewModel::unfollow,
                state = state)
            HorizontalDivider(padding = 2.dp)
        }

        state.challenge.description?.apply {
            item {
                ShowChallengeDescription(this@apply)
            }
        }

        items(state.claims.size) { index: Int ->
            ClaimCard(retrieveUser = viewModel::retrieveUser, state.claims[index], fnViewImageCallback)
        }
    }
}

/**
 * @brief Show a card containing the challenge description that is expandable
 *
 * @param challengeDescription the description of the challenge to be displayed
 */
@Composable
fun ShowChallengeDescription(challengeDescription: String) {
    var isDescriptionExpanded by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = if (isDescriptionExpanded) Int.MAX_VALUE.dp else 90.dp)
            .padding(10.dp),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                challengeDescription,
                fontSize = 11.sp,
                color = MaterialTheme.colors.onBackground,
                maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 2,
                modifier = Modifier.padding(10.dp),
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text =  stringResource(if (isDescriptionExpanded) R.string.less_button
                                                             else R.string.more_button),
                fontSize = 11.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 5.dp)
                    .testTag("description-more-btn")
                    .clickable {
                        isDescriptionExpanded = !isDescriptionExpanded
                    }
            )
        }
    }
}
