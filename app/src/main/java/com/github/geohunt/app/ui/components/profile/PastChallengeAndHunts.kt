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

typealias ComposableFun = @Composable (User) -> Unit

enum class Tabs(val tabName: Int, val tabContent: ComposableFun) {
    PastChallenges(R.string.challenges, { PastChallengesContent(it) }),
    PastHunts(R.string.hunts, { PastHuntsContent(it) })
}

@Composable
fun PastChallengeAndHunts(user: User) {
    var currentTab by remember { mutableStateOf(Tabs.PastChallenges) }

    Column {
        TabRow(selectedTabIndex = currentTab.ordinal, backgroundColor = MaterialTheme.colors.background) {
            Tabs.values().forEach {
                Tab(
                    selected = it.ordinal == currentTab.ordinal,
                    text = { Text(stringResource(id = it.tabName)) },
                    onClick = { currentTab = it },
                    modifier = Modifier.testTag("tabbtn-${it.ordinal}")
                )
            }
        }

        currentTab.tabContent(user)
    }
}

@Composable
fun PastChallengesContent(user: User) {
    if (user.challenges.isEmpty()) {
        CenteredText(str = stringResource(id = R.string.no_past_challenges))
    } else {
        ChallengeGrid(challenges = user.challenges)
    }
}

@Composable
fun PastHuntsContent(user: User) {
    if (user.challenges.isEmpty()) {
        CenteredText(str = stringResource(id = R.string.no_past_hunts))
    } else {
        ChallengeGrid(challenges = user.challenges)
    }
}

@Composable
private fun CenteredText(str: String) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Text(str, textAlign = TextAlign.Center)
    }
}