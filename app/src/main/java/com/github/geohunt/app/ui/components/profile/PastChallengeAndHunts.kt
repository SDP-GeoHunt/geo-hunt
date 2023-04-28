package com.github.geohunt.app.ui.components.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.R
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.LazyRef

typealias ComposableFun = @Composable (ProfilePageViewModel) -> Unit

enum class ProfileTabs(val tabName: Int, val tabContent: ComposableFun) {
    PastChallenges(R.string.challenges, { PastChallengesContent(it) }),
    PastHunts(R.string.hunts, { PastHuntsContent(it) })
}

/**
 * Shows a tab view of two different tabs, for past challenges and past hunts
 */
@Composable
fun PastChallengeAndHunts(vm: ProfilePageViewModel) {
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

        currentTab.tabContent(vm)
    }
}

/**
 * A grid for showing past challenges
 */
@Composable
fun PastChallengesContent(vm: ProfilePageViewModel) {
    val challenges = vm.challenges.collectAsState()

    MakeGrid(testTag = "past-challenges", challenges = challenges.value ?: listOf(), whenEmptyText = stringResource(id = R.string.no_past_challenges))
}

/**
 * A grid for showing past hunts
 */
@Composable
fun PastHuntsContent(vm: ProfilePageViewModel) {
    val hunts = vm.claimedChallenges.collectAsState()

    MakeGrid(testTag = "past-hunts", challenges = hunts.value ?: listOf(), whenEmptyText = stringResource(id = R.string.no_past_hunts))
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