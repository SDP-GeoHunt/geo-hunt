package com.github.geohunt.app.ui.components.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.github.geohunt.app.mocks.InstantLazyRef
import com.github.geohunt.app.mocks.MockChallenge
import com.github.geohunt.app.mocks.MockLazyRef
import com.github.geohunt.app.mocks.MockUser
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.google.android.gms.tasks.Tasks
import org.junit.Rule
import org.junit.Test

class ProfilePage {
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
        val mockuser = MockUser(hunts = listOf(
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
}