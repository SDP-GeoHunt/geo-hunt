package com.github.geohunt.app.ui.components.activehunts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.model.Challenge
import kotlinx.coroutines.flow.StateFlow

/**
 * Creates a scrollable list of active hunts using the given list.
 *
 * If the list is empty, uses the [EmptyChallengeScreen] as fallback.
 *
 * @param challenges The challenges to display.
 * @param openExploreTab The function called to open the explore view in the navigation.
 */
@Composable
fun ActiveHuntsList(
    challenges: List<Challenge>,
    openExploreTab: () -> Unit,
    getAuthorName: (Challenge) -> StateFlow<String>
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (challenges.isEmpty()) {
            EmptyChallengesScreen(openExploreTab)
        } else {
            LazyRow(
                modifier = Modifier.testTag("challenge_row"),
                contentPadding = PaddingValues(30.dp, 0.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(challenges) { challenge ->
                    Box(modifier = Modifier.size(300.dp, 600.dp)) {
                        ChallengePreview(challenge = challenge, getAuthorName = getAuthorName)
                    }
                }
            }
        }

    }
}