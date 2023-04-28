package com.github.geohunt.app.ui.components.profile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.geohunt.app.mocks.InstantLazyRef
import com.github.geohunt.app.mocks.MockChallenge
import com.github.geohunt.app.mocks.MockLazyRef
import com.github.geohunt.app.mocks.MockUser
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.google.android.gms.tasks.Tasks
import org.junit.Rule
import org.junit.Test

class ProfilePageTest {
    @get:Rule
    val testRule = createComposeRule()

    @Test
    fun showsLoadingIfNotReady() {
        testRule.setContent {
            ProfilePage(user = InstantLazyRef("1", null))
        }
        testRule.onNodeWithTag("progress").assertIsDisplayed()
    }

    @Test
    fun doesNotShowLoadingIfReady() {
        testRule.setContent {
            ProfilePage(user = InstantLazyRef("1", MockUser()))
        }
        testRule.onNodeWithTag("progress").assertDoesNotExist()
    }

    @Test
    fun showsScore() {
        testRule.setContent {
            ProfilePage(user = InstantLazyRef("1", MockUser(score = 2321)))
        }
        testRule.onNodeWithText("2321").assertExists()
    }

    @Test
    fun showsDisplayName() {
        testRule.setContent {
            ProfilePage(user = InstantLazyRef("1", MockUser(displayName = "coucou")))
        }
        testRule.onNodeWithText("coucou").assertExists()
    }

    @Test
    fun showsNumberOfHunts() {
        val mockuser = MockUser(activeHunts = listOf(
            wrapLazyChallenge(MockChallenge()),
            wrapLazyChallenge(MockChallenge()),
            wrapLazyChallenge(MockChallenge())
        ))

        testRule.setContent {
            ProfilePage(user = InstantLazyRef("1", mockuser) )
        }
        testRule.onNodeWithText(mockuser.challenges.size.toString()).assertExists()
    }

    @Test
    fun showsNumberOfChallenges() {
        val mockuser = MockUser(challenges = listOf(
            wrapLazyChallenge(MockChallenge()),
            wrapLazyChallenge(MockChallenge()),
            wrapLazyChallenge(MockChallenge())
        ))
        testRule.setContent {
            ProfilePage(user = InstantLazyRef("1", mockuser))
        }
        testRule.onNodeWithText(mockuser.challenges.size.toString()).assertExists()
    }

    private fun wrapLazyChallenge(challenge: Challenge): LazyRef<Challenge> {
        return MockLazyRef("1") { Tasks.forResult(challenge) }
    }

    @Test
    fun doesNotShowSettingsBtnIfNotNeeded() {
        testRule.setContent {
            ProfilePage(user = InstantLazyRef("1", MockUser()))
        }
        testRule.onNodeWithTag("profile-settings-btn").assertDoesNotExist()
    }

    @Test
    fun showsSettingsBtnIfAvailable1() {
        testRule.setContent {
            ProfilePage(user = InstantLazyRef("1", MockUser()), { })
        }
        testRule.onNodeWithTag("profile-settings-btn").assertExists()
    }

    @Test
    fun showsSettingsBtnIfAvailable2() {
        testRule.setContent {
            ProfilePage(user = InstantLazyRef("1", MockUser()), null, { })
        }
        testRule.onNodeWithTag("profile-settings-btn").assertExists()
    }

    @Test
    fun showsSettingsBtnIfAvailable3() {
        testRule.setContent {
            ProfilePage(user = InstantLazyRef("1", MockUser()), null, null, { })
        }
        testRule.onNodeWithTag("profile-settings-btn").assertExists()
    }

    @Test
    fun clickingOnSettingsBtnShowsDrawer() {
        testRule.setContent {
            ProfilePage(user = InstantLazyRef("1", MockUser()), { })
        }
        testRule.onNodeWithTag("settings-drawer").assertIsNotDisplayed()
        testRule.onNodeWithTag("profile-settings-btn").performClick()
        testRule.onNodeWithTag("settings-drawer").assertIsDisplayed()
    }
}