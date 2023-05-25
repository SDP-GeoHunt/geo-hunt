package com.github.geohunt.app.ui.components.bounties

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.geohunt.app.model.Bounty
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class TestBountyCreation {
    @get:Rule
    val testRule = createComposeRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testBountyCreationHasCorrectSettings() = runTest {
        val completableDeferred = CompletableDeferred<Bounty>()

        testRule.setContent {
            CreateNewBounty(onFailure = completableDeferred::completeExceptionally,
                            onSuccess = completableDeferred::complete)
        }

        testRule.onNodeWithTag("create-btn")
            .assertIsDisplayed()
            .assertIsNotEnabled()

        testRule.onNodeWithTag("bounty-name-field")
            .assertIsDisplayed()
            .performTextInput("Lausanne Cup's")

        testRule.onNodeWithTag("date-field")
            .assertIsDisplayed()
            .performClick()

        /* Select the date */
        testRule.awaitIdle()

        testRule.onNodeWithText("5").performClick()
        testRule.onNodeWithText("10").performClick()
        testRule.onNodeWithText("OK").performClick()

        /* Select the location */
        testRule.awaitIdle()
        testRule.onNodeWithTag("location-picker-field")
            .assertIsDisplayed()
            .performClick()
    }

}
