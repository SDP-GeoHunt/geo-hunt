package com.github.geohunt.app.ui.components.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.github.geohunt.app.R
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.ui.components.GeoHuntTabs
import com.github.geohunt.app.ui.components.TabData

enum class ProfileTabs(val title: Int) {
    PastChallenges(R.string.challenges),
    PastHunts(R.string.hunts)
}

/**
 * Shows a tab view of two different tabs, for past challenges and past hunts
 */
@Composable
fun PastChallengeAndHunts(challenges: List<Challenge>?, hunts: List<Challenge>?, openChallengeView: (Challenge) -> Unit) {
    var currentTab by remember { mutableStateOf(ProfileTabs.PastChallenges) }

    Column {
        // TODO Using GeoHuntTabs breaks ProfilePageTests
        TabRow(selectedTabIndex = currentTab.ordinal, backgroundColor = MaterialTheme.colors.background) {
            ProfileTabs.values().forEach {
                Tab(
                    selected = it.ordinal == currentTab.ordinal,
                    text = { Text(stringResource(id = it.title)) },
                    onClick = { currentTab = it },
                    modifier = Modifier.testTag("tabbtn-${it.ordinal}")
                )
            }
        }

        when (currentTab) {
            ProfileTabs.PastChallenges -> PastChallengesContent(challenges, openChallengeView)
            ProfileTabs.PastHunts -> PastHuntsContent(hunts, openChallengeView)
        }
    }
}

/**
 * A grid for showing past challenges
 */
@Composable
fun PastChallengesContent(challenges: List<Challenge>?, openChallengeView: (Challenge) -> Unit) {
    if (challenges == null)
        CircularProgressIndicator()
    else
        MakeGrid(testTag = "past-challenges", challenges = challenges, whenEmptyText = stringResource(id = R.string.no_past_challenges), openChallengeView)
}

/**
 * A grid for showing past hunts
 */
@Composable
fun PastHuntsContent(hunts: List<Challenge>?, openChallengeView: (Challenge) -> Unit) {
    if (hunts == null)
        CircularProgressIndicator()
    else
        MakeGrid(testTag = "past-hunts", challenges = hunts, whenEmptyText = stringResource(id = R.string.no_past_hunts), openChallengeView)
}

/**
 * A more general grid used to display challenges
 */
@Composable
private fun MakeGrid(testTag: String, challenges: List<Challenge>, whenEmptyText: String, openChallengeView: (Challenge) -> Unit) {
    Box(modifier = Modifier.testTag(testTag)) {
        if (challenges.isEmpty()) {
            CenteredText(str = whenEmptyText)
        } else {
            ChallengeGrid(challenges = challenges, openChallengeView)
        }
    }
}

@Composable
private fun CenteredText(str: String) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Text(str, textAlign = TextAlign.Center)
    }
}