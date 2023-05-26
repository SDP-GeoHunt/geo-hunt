package com.github.geohunt.app.ui.components.buttons

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class MenuButtonTest {
    @get:Rule
    val testRule = createComposeRule()

    @Test
    fun menuButtonTitleIsDisplayed() {
        testRule.setContent {
            MenuItem(title = "displayed", icon = { }, onClick = { })
        }
        testRule.onNodeWithText("displayed", useUnmergedTree = true)
                .assertIsDisplayed()
    }

    @Test
    fun menuButtonExecutesCallback() {
        var callbackCalled = false
        testRule.setContent {
            MenuItem(title = "test", icon = { }, onClick = { callbackCalled = true })
        }

        testRule.onNodeWithText("test", useUnmergedTree = true)
                .onParent()
                .assertHasClickAction()
                .performClick()

        Assert.assertTrue(callbackCalled)
    }

    @Test
    fun iconComposableIsCalled() {
        testRule.setContent {
            MenuItem(title = "yes", icon = { Text(text = "maybe") }, onClick = { })
        }
        testRule.onNodeWithText("maybe", useUnmergedTree = true)
                .assertIsDisplayed()
    }

    @Test
    fun menuButtonIconIsDisplayed() {
        testRule.setContent {
            MenuButton {
                Column {
                    Text("yes")
                }
            }
        }
        testRule.onNodeWithContentDescription("More actions", useUnmergedTree = true)
                .assertIsDisplayed()
    }

    @Test
    fun menuButtonDisplaysContentOnClick() {
        testRule.setContent {
            MenuButton {
                Column {
                    Text("yes")
                    Text("maybe")
                }
            }
        }
        testRule.onNodeWithContentDescription("More actions", useUnmergedTree = true)
                .assertIsDisplayed()
                .onParent()
                .assertHasClickAction()
                .performClick()

        testRule.onNodeWithText("yes").assertIsDisplayed()
        testRule.onNodeWithText("maybe").assertIsDisplayed()
    }
}