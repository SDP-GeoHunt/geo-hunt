package com.github.geohunt.app.ui.components.buttons

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class LikeButtonTest {
    @get:Rule
    val testRule = createComposeRule()

    @Test
    fun likeButtonIsClickable() {
        var callbackCalled = true
        testRule.setContent {
            LikeButton(isLiked = true, onLikeChanged = { callbackCalled = it })
        }

        testRule.onNodeWithContentDescription("Like this challenge", useUnmergedTree = true)
                .assertIsDisplayed()
                .onParent()
                .assertHasClickAction()
                .performClick()

        Assert.assertFalse(callbackCalled)
    }

    @Test
    fun likeButtonIsClickable2() {
        var callbackCalled = false
        testRule.setContent {
            LikeButton(isLiked = false, onLikeChanged = { callbackCalled = it })
        }

        testRule.onNodeWithContentDescription("Unlike this challenge", useUnmergedTree = true)
                .assertIsDisplayed()
                .onParent()
                .assertHasClickAction()
                .performClick()

        Assert.assertTrue(callbackCalled)
    }
}