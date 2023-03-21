package com.github.geohunt.app.ui.components.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.*
import com.github.geohunt.app.model.database.api.User

typealias ComposableFun = @Composable (User) -> Unit

enum class Tabs(val tabName: String, val tabContent: ComposableFun) {
    PastChallenges("Challenges", { PastChallengesContent(it) }),
    PastHunts("Hunts", { PastHuntsContent(it) })
}

@Composable
fun PastChallengeAndHunts(user: User) {
    var currentTab by remember { mutableStateOf(Tabs.PastChallenges) }

    Column {
        TabRow(selectedTabIndex = currentTab.ordinal) {
            Tabs.values().forEach {
                Tab(
                    selected = it.ordinal == currentTab.ordinal,
                    text = { Text(it.name) },
                    onClick = { currentTab = it }
                )
            }
        }

        currentTab.tabContent(user)
    }
}

@Composable
fun PastChallengesContent(user: User) {
    Text("Nothing to see here.")
}

@Composable
fun PastHuntsContent(user: User) {
    Text("Nothing to see here, too.")
}