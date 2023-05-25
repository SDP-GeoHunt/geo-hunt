package com.github.geohunt.app.ui.components.activehunts

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class EmptyScreenTest {
    @get:Rule
    val testRule = createComposeRule()

    @Test
    fun textsAreDisplayedCorrectly() {
        testRule.setContent {
            EmptyScreen(text = "text1", buttonText = "text2") { }
        }

        testRule.onNodeWithText("text1").assertIsDisplayed()
        testRule.onNodeWithText("text2", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun callbackIsCalledOnButtonClicK() {
        var callbackCalled = false
        testRule.setContent {
            EmptyScreen(text = "not a button", buttonText = "a button") {
                callbackCalled = true
            }
        }

        testRule.onNodeWithText("a button", useUnmergedTree = true)
                .onParent()
                .assertHasClickAction()
                .performClick()

        Assert.assertTrue(callbackCalled)
    }
}