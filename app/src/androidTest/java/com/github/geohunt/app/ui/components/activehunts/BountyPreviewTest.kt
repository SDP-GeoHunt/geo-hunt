package com.github.geohunt.app.ui.components.activehunts

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.github.geohunt.app.mocks.MockBounty
import com.github.geohunt.app.mocks.MockChallenge
import com.github.geohunt.app.model.Bounty
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

class BountyPreviewTest {
    @get:Rule
    val testRule = createComposeRule()

    private val dummyBounty = MockBounty()
    private val dummyChallenge = MockChallenge()

    fun setupComposable(bounty: Bounty = dummyBounty) {
        testRule.setContent {
            BountyPreview(bounty, dummyChallenge)
        }
    }

    @Test
    fun iconsAreDisplayed() {
        setupComposable()

        testRule.onAllNodesWithContentDescription("icon", substring = true).assertCountEquals(2)
        testRule.onNodeWithContentDescription("bounty", substring = true).assertIsDisplayed()
        testRule.onNodeWithContentDescription("calendar", substring = true).assertIsDisplayed()
    }

    @Test
    fun challengeNameIsDisplayed() {
        setupComposable()

        testRule.onNodeWithText("Nice bounty").assertIsDisplayed()
    }

    @Test
    fun dateBeforeBountyIsCorrect() {
        setupComposable(MockBounty(startingDate = LocalDateTime.now().plusDays(5).plusHours(12)))

        testRule.onNodeWithText("Starts in 5 days").assertIsDisplayed()
    }

    @Test
    fun dateDuringBountyIsCorrect() {
        setupComposable(MockBounty(expirationDate = LocalDateTime.now().plusDays(5).plusHours(12)))

        testRule.onNodeWithText("Ends in 5 days").assertIsDisplayed()
    }

    @Test
    fun dateAfterBountyIsCorrect() {
        setupComposable(MockBounty(expirationDate = LocalDateTime.now().minusDays(5).minusHours(12)))

        testRule.onNodeWithText("Expired 5 days ago").assertIsDisplayed()
    }
}