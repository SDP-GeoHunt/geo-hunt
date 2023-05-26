package com.github.geohunt.app.ui.components.buttons

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class OpenMapButtonTest {
    @get:Rule
    val testRule = createComposeRule()

    @Test
    fun openMapButtonCallbackIsCalled() {
        var callbackCalled = false
        testRule.setContent {
            OpenMapButton {
                callbackCalled = true
            }
        }

        testRule.onNodeWithContentDescription("Open the map", useUnmergedTree = true)
                .onParent()
                .assertHasClickAction()
                .performClick()

        Assert.assertTrue(callbackCalled)
    }
}