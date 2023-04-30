package com.github.geohunt.app.ui.components.challenge

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.geohunt.app.R
import com.github.geohunt.app.model.database.Database
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.ui.components.AsyncImage
import com.github.geohunt.app.ui.components.GoBackBtn
import com.github.geohunt.app.ui.screens.home.HorizontalDivider

/**
 * @brief Composable function that displays a specific challenge including details
 * such as previous claims and the author to the user.
 *
 * @param challenge the challenge to be displayed to the user
 * @param fnViewImageCallback function called in order to open the view for a specific image
 * @param fnGoBackBtn function called when user presses the go back button
 */
@Composable
fun ChallengeView(
    challenge: Challenge,
    database: Database,
    user: User,
    fnViewImageCallback: (String) -> Unit,
    fnGoBackBtn: () -> Unit
) {
    val lazyState = rememberLazyListState()
    val transition = updateTransition(
        remember { derivedStateOf { lazyState.firstVisibleItemIndex != 0 } },
        label = "Image size transition"
    )
    val imageAspectRatio by transition.animateFloat(label = "Animate image size ratio") { isScrolling ->
        if (isScrolling.value) 1.8f else 1.0f
    }

    Box {
        Column {
            // Main challenge image
            AsyncImage(
                contentDescription = "Challenge Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        fnViewImageCallback(challenge.thumbnail.id)
                    }
                    .aspectRatio(imageAspectRatio, false),
                contentScale = ContentScale.Crop
            ) {
                challenge.thumbnail
            }

            BellowImageButtons(
                challenge = challenge,
                user = user,
                database = database
            )

            Spacer(Modifier.height(2.dp))

            HorizontalDivider(padding = 2.dp)

            LazyClaimList(lazyState, challenge, fnViewImageCallback)
        }

        // Go back button (for navigation)
        GoBackBtn(fnGoBackBtn)
    }
}

@Composable
private fun LazyClaimList(
    lazyState: LazyListState,
    challenge: Challenge,
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
            MainUserView(challenge = challenge)
            HorizontalDivider(padding = 2.dp)
        }

        challenge.description?.apply {
            item {
                ShowChallengeDescription(this@apply)
            }
        }

        items(challenge.claims.size) { index: Int ->
            ClaimCard(claimRef = challenge.claims[index], fnViewImageCallback)
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
        elevation = 10.dp
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
