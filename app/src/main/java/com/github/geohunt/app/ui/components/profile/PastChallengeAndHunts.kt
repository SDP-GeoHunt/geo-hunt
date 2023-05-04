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

typealias ComposableFun = @Composable (List<Challenge>?) -> Unit

enum class ProfileTabs(val tabName: Int, val tabContent: ComposableFun) {
    PastChallenges(R.string.challenges, { PastChallengesContent(it) }),
    PastHunts(R.string.hunts, { PastHuntsContent(it) })
}

/**
 * Shows a tab view of two different tabs, for past challenges and past hunts
 */
@Composable
fun PastChallengeAndHunts(challenges: List<Challenge>?, hunts: List<Challenge>?) {
    var currentTab by remember { mutableStateOf(ProfileTabs.PastChallenges) }

    Column {
        TabRow(selectedTabIndex = currentTab.ordinal, backgroundColor = MaterialTheme.colors.background) {
            ProfileTabs.values().forEach {
                Tab(
                    selected = it.ordinal == currentTab.ordinal,
                    text = { Text(stringResource(id = it.tabName)) },
                    onClick = { currentTab = it },
                    modifier = Modifier.testTag("tabbtn-${it.ordinal}")
                )
            }
        }

        currentTab.tabContent(if (currentTab.ordinal == 0) challenges else hunts)
    }
}

/**
 * A grid for showing past challenges
 */
@Composable
fun PastChallengesContent(challenges: List<Challenge>?) {
    if (challenges == null)
        CircularProgressIndicator()
    else
        MakeGrid(testTag = "past-challenges", challenges = challenges, whenEmptyText = stringResource(id = R.string.no_past_challenges))
}

/**
 * A grid for showing past hunts
 */
@Composable
fun PastHuntsContent(hunts: List<Challenge>?) {
    if (hunts == null)
        CircularProgressIndicator()
    else
        MakeGrid(testTag = "past-hunts", challenges = hunts, whenEmptyText = stringResource(id = R.string.no_past_hunts))
}

/**
 * A more general grid used to display challenges
 */
@Composable
private fun MakeGrid(testTag: String, challenges: List<Challenge>, whenEmptyText: String) {
    Box(modifier = Modifier.testTag(testTag)) {
        if (challenges.isEmpty()) {
            CenteredText(str = whenEmptyText)
        } else {
            ChallengeGrid(challenges = challenges)
        }
    }
}

@Composable
private fun CenteredText(str: String) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Text(str, textAlign = TextAlign.Center)
    }
}