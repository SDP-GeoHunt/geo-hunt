package com.github.geohunt.app.ui.components.cards

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.doubleClick
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import org.junit.Assert.*
import org.junit.Rule

import org.junit.Test

/**
 * For isolation reasons, we decided not to test the loading of images from the network, as it may
 * make them flaky.
 */
class ChallengeCardImageTest {
    @get:Rule
    val testRule = createComposeRule()

    @Test
    fun onClickIsNotCalledWhenLoading() {
        var isClicked = false

        testRule.setContent { 
            ChallengeCardImage(url = null, onClick = { isClicked = true }, onDoubleTap = {})
        }

        testRule.onNodeWithTag("skeleton")
            .assertIsDisplayed()
            .performClick()

        assertFalse(isClicked)
    }

    @Test
    fun onDoubleTapIsNotCalledWhenLoading() {
        var isDoubleTapped = false

        testRule.setContent {
            ChallengeCardImage(url = null, onClick = {}, onDoubleTap = { isDoubleTapped = true })
        }

        testRule.onNodeWithTag("skeleton")
            .assertIsDisplayed()
            .performTouchInput { doubleClick() }

        assertFalse(isDoubleTapped)
    }
}