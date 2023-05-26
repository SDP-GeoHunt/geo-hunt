package com.github.geohunt.app.ui.components.cards

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import com.github.geohunt.app.ui.components.buttons.ChallengeHuntState
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ChallengeCardActionsTest {

    @get:Rule
    val testRule = createComposeRule()

    @Test
    fun onOpenMapIsCalledOnClickingMapButton() {
        var isMapOpen = false

        testRule.setContent {
            ChallengeCardActions(
                huntState = ChallengeHuntState.NOT_HUNTED,
                onOpenMap = { isMapOpen = true },
                onHunt = {},
                onClaim = {},
                isBusy = { false }
            )
        }

        testRule.onNodeWithContentDescription("Open the map").onParent()
            .assertIsDisplayed()
            .performClick()

        assertTrue(isMapOpen)
    }

    @Test
    fun onHuntIsCalledOnClickingHuntButton() {
        var isHunted = false

        testRule.setContent {
            ChallengeCardActions(
                huntState = ChallengeHuntState.NOT_HUNTED,
                onOpenMap = {},
                onHunt = { isHunted = true },
                onClaim = {},
                isBusy = { false }
            )
        }

        testRule.onNodeWithContentDescription("Hunt").onParent()
            .assertIsDisplayed()
            .performClick()

        assertTrue(isHunted)
    }

    @Test
    fun onClaimIsCalledOnClickingClaimButton() {
        var isClaimed = false

        testRule.setContent {
            ChallengeCardActions(
                huntState = ChallengeHuntState.HUNTED,
                onOpenMap = {},
                onHunt = {},
                onClaim = { isClaimed = true },
                isBusy = { false }
            )
        }

        testRule.onNodeWithContentDescription("Claim").onParent()
            .assertIsDisplayed()
            .performClick()

        assertTrue(isClaimed)
    }

    @Test
    fun buttonIsDisabledWhenBusy() {
        testRule.setContent {
            ChallengeCardActions(
                huntState = ChallengeHuntState.HUNTED,
                onOpenMap = {},
                onHunt = {},
                onClaim = {},
                isBusy = { true }
            )
        }

        testRule.onNodeWithContentDescription("Claim").onParent()
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }
}