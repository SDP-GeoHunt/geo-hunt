package com.github.geohunt.app.ui.components.profile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.geohunt.app.mocks.InstantLazyRef
import com.github.geohunt.app.mocks.MockChallenge
import com.github.geohunt.app.mocks.MockUser
import org.junit.Rule
import org.junit.Test

class PastChallengesAndHuntsTest {
    @get:Rule
    val testRule = createComposeRule()

    // Navigation test
    @Test
    fun clickingOnTabSelectsIt() {
        testRule.setContent {
            PastChallengeAndHunts(user = MockUser())
        }
        for (tab in ProfileTabs.values()) {
            val node = testRule.onNodeWithTag("tabbtn-${tab.ordinal}")
            node.performClick()
            node.assertIsSelected()
        }
    }

    @Test
    fun clickingOnPastHuntsShowsPastHuntsComponent() {
        testRule.setContent {
            PastChallengeAndHunts(user = MockUser())
        }
        testRule.onNodeWithTag("tabbtn-${ProfileTabs.PastHunts.ordinal}").performClick()
        testRule.onNodeWithTag("past-hunts").assertExists().assertIsDisplayed()
    }

    @Test
    fun clickingOnPastChallengesShowsPastChallengesComponent() {
        testRule.setContent {
            PastChallengeAndHunts(user = MockUser())
        }
        testRule.onNodeWithTag("tabbtn-${ProfileTabs.PastChallenges.ordinal}").performClick()
        testRule.onNodeWithTag("past-challenges").assertExists().assertIsDisplayed()
    }
    
    @Test
    fun showsEmptyMessageWhenNoPastHunts() {
        testRule.setContent {
            PastChallengeAndHunts(user = MockUser())
        }
        testRule.onNodeWithTag("tabbtn-${ProfileTabs.PastHunts.ordinal}").performClick()
        testRule.onNodeWithText("No past hunts", substring = true).assertIsDisplayed()
    }

    @Test
    fun showsEmptyMessageWhenNoPastChallenges() {
        testRule.setContent {
            PastChallengeAndHunts(user = MockUser())
        }
        testRule.onNodeWithTag("tabbtn-${ProfileTabs.PastChallenges.ordinal}").performClick()
        testRule.onNodeWithText("No challenges", substring = true).assertIsDisplayed()
    }

    @Test
    fun doesNotShowEmptyMessageWhenNoPastHunts() {
        testRule.setContent {
            PastChallengeAndHunts(user = MockUser(activeHunts = listOf(InstantLazyRef("1", MockChallenge()))))
        }
        testRule.onNodeWithTag("tabbtn-${ProfileTabs.PastHunts.ordinal}").performClick()
        testRule.onNodeWithText("No past hunts", substring = true).assertDoesNotExist()
    }

    @Test
    fun doesNotShowEmptyMessageWhenNoPastChallenges() {
        testRule.setContent {
            PastChallengeAndHunts(user = MockUser(challenges = listOf(InstantLazyRef("1", MockChallenge()))))
        }
        testRule.onNodeWithTag("tabbtn-${ProfileTabs.PastChallenges.ordinal}").performClick()
        testRule.onNodeWithText("No challenges", substring = true).assertDoesNotExist()
    }
}