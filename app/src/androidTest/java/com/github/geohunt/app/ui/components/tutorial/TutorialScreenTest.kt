package com.github.geohunt.app.ui.components.tutorial

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TutorialScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        composeTestRule.setContent {
            Tutorial(activity = ComponentActivity())
        }
    }

    @Test
    fun checkLayoutAndButtonsAreShowed() {
        composeTestRule
            .onNodeWithTag("Tutorial screen layout")
            .assertExists()

        composeTestRule
            .onNodeWithTag("Go forward button")
            .assertExists()
            // Perform click to go to next screen and display the back button
            .performClick()

        composeTestRule
            .onNodeWithTag("Go back button")
            .assertExists()

        composeTestRule
            .onNodeWithTag("Skip button")
            .assertExists()
    }

    @Test
    fun clickingOnForwardAndBackButtonsChangesThePage() {
        composeTestRule
            .onNodeWithTag("Current page is 0")
            .assertExists()

        composeTestRule
            .onNodeWithTag("Go forward button")
            .assertHasClickAction()
            .performClick()

        composeTestRule
            .onNodeWithTag("Current page is 1")
            .assertExists()

        composeTestRule
            .onNodeWithTag("Go forward button")
            .assertHasClickAction()
            .performClick()

        composeTestRule
            .onNodeWithTag("Current page is 2")
            .assertExists()

        composeTestRule
            .onNodeWithTag("Go back button")
            .assertHasClickAction()
            .performClick()

        composeTestRule
            .onNodeWithTag("Current page is 1")
            .assertExists()

        composeTestRule
            .onNodeWithTag("Go back button")
            .assertHasClickAction()
            .performClick()

        composeTestRule
            .onNodeWithTag("Current page is 0")
            .assertExists()
    }

    @Test
    fun checkSlideContentIsCorrectlyDisplayed() {
        composeTestRule
            .onNodeWithTag("Tutorial Image")
            .assertExists()

        composeTestRule
            .onNodeWithTag("Tutorial Title")
            .assertExists()

        composeTestRule
            .onNodeWithTag("Tutorial Description")
            .assertExists()
    }
}