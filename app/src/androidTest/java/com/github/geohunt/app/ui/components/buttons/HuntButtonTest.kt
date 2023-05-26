package com.github.geohunt.app.ui.components.buttons

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class HuntButtonTest {
    @get:Rule
    val testRule = createComposeRule()

    @Test
    fun huntButtonCallbackIsCalled() {
        var callbackCalled = false
        testRule.setContent {
            HuntButton(onClick = { callbackCalled = true })
        }
        testRule.onNodeWithText("Hunt", useUnmergedTree = true)
                .onParent()
                .assertHasClickAction()
                .performClick()
        assertTrue(callbackCalled)
    }

    @Test
    fun huntTextIsShown() {
        testRule.setContent {
            HuntButton(onClick = { })
        }

        testRule.onNodeWithText("Hunt", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun huntButtonCanBeDisabled() {
        var callbackCalled = false
        testRule.setContent {
            HuntButton(onClick = { callbackCalled = true }, false)
        }

        testRule.onNodeWithText("Hunt", useUnmergedTree = true)
                .onParent()
                .assertHasClickAction()
                .performClick()
        assertFalse(callbackCalled)
    }

    @Test
    fun claimButtonCallbackIsCalled() {
        var callbackCalled = false
        testRule.setContent {
            ClaimButton(onClick = { callbackCalled = true })
        }
        testRule.onNodeWithText("Claim", useUnmergedTree = true)
                .onParent()
                .assertHasClickAction()
                .performClick()
        assertTrue(callbackCalled)
    }

    @Test
    fun claimTextIsShown() {
        testRule.setContent {
            ClaimButton(onClick = { })
        }

        testRule.onNodeWithText("Claim", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun claimButtonCanBeDisabled() {
        var callbackCalled = false
        testRule.setContent {
            ClaimButton(onClick = { callbackCalled = true }, false)
        }

        testRule.onNodeWithText("Claim", useUnmergedTree = true)
                .onParent()
                .assertHasClickAction()
                .performClick()
        assertFalse(callbackCalled)
    }

    @Test
    fun claimedButtonCallbackIsCalled() {
        var callbackCalled = false
        testRule.setContent {
            ClaimedButton(onClick = { callbackCalled = true })
        }
        testRule.onNodeWithText("Claimed", useUnmergedTree = true)
                .onParent()
                .assertHasClickAction()
                .performClick()
        assertTrue(callbackCalled)
    }

    @Test
    fun claimedTextIsShown() {
        testRule.setContent {
            ClaimedButton(onClick = { })
        }

        testRule.onNodeWithText("Claimed", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun huntClaimButtonDisplaysHuntButton() {
        testRule.setContent {
            HuntClaimButton(state = ChallengeHuntState.NOT_HUNTED, onHunt = { }, onClaim = { })
        }

        testRule.onNodeWithText("Hunt", useUnmergedTree = true)
                .assertIsDisplayed()
    }

    @Test
    fun huntClaimButtonDisplaysClaimButton() {
        testRule.setContent {
            HuntClaimButton(state = ChallengeHuntState.HUNTED, onHunt = { }, onClaim = { })
        }

        testRule.onNodeWithText("Claim", useUnmergedTree = true)
                .assertIsDisplayed()
    }

    @Test
    fun huntClaimButtonDisplaysClaimedButton() {
        testRule.setContent {
            HuntClaimButton(state = ChallengeHuntState.CLAIMED, onHunt = { }, onClaim = { })
        }

        testRule.onNodeWithText("Claimed", useUnmergedTree = true)
                .assertIsDisplayed()
    }

    @Test
    fun huntClaimButtonDisplaysHuntButtonOnUnknownState() {
        testRule.setContent {
            HuntClaimButton(state = ChallengeHuntState.UNKNOWN, onHunt = { }, onClaim = { })
        }

        testRule.onNodeWithText("Hunt", useUnmergedTree = true)
                .assertIsDisplayed()
    }
}