package com.github.geohunt.app.ui.components.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.github.geohunt.app.mocks.MockChallenge
import org.junit.Rule
import org.junit.Test

class PastChallengesAndHuntsTest {
    @get:Rule
    val testRule = createComposeRule()

    // Navigation test
    @Test
    fun clickingOnPastHuntsShowsPastHuntsComponent() {
        testRule.setContent {
            PastChallengeAndHunts(listOf(), listOf(), {})
        }
        testRule.onNodeWithTag("tab-${ProfileTabs.PastHunts.ordinal}").performClick()
        testRule.onNodeWithTag("past-hunts").assertExists().assertIsDisplayed()
    }

    @Test
    fun clickingOnPastChallengesShowsPastChallengesComponent() {
        testRule.setContent {
            PastChallengeAndHunts(listOf(), listOf(), {})
        }
        testRule.onNodeWithTag("tab-${ProfileTabs.PastChallenges.ordinal}").performClick()
        testRule.onNodeWithTag("past-challenges").assertExists().assertIsDisplayed()
    }
    
    @Test
    fun showsEmptyMessageWhenNoPastHunts() {
        testRule.setContent {
            PastChallengeAndHunts(listOf(MockChallenge()), listOf(), {})
        }
        testRule.onNodeWithTag("tab-${ProfileTabs.PastHunts.ordinal}").performClick()
        testRule.onNodeWithText("No past hunts", substring = true).assertIsDisplayed()
    }

    @Test
    fun showsEmptyMessageWhenNoPastChallenges() {
        testRule.setContent {
            PastChallengeAndHunts(listOf(), listOf(), {})
        }
        testRule.onNodeWithTag("tab-${ProfileTabs.PastChallenges.ordinal}").performClick()
        testRule.onNodeWithText("No challenges", substring = true).assertIsDisplayed()
    }

    @Test
    fun doesNotShowEmptyMessageWhenNoPastHunts() {
        testRule.setContent {
            PastChallengeAndHunts(hunts = listOf(MockChallenge()), challenges = listOf(),
                openChallengeView = {})
        }
        testRule.onNodeWithTag("tab-${ProfileTabs.PastHunts.ordinal}").performClick()
        testRule.onNodeWithText("No past hunts", substring = true).assertDoesNotExist()
    }

    @Test
    fun doesNotShowEmptyMessageWhenNoPastChallenges() {
        testRule.setContent {
            PastChallengeAndHunts(challenges = listOf(MockChallenge()), hunts = listOf(), {})
        }
        testRule.onNodeWithTag("tab-${ProfileTabs.PastChallenges.ordinal}").performClick()
        testRule.onNodeWithText("No challenges", substring = true).assertDoesNotExist()
    }
}