package com.github.geohunt.app.ui.components.tutorial

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
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
    fun checkLayoutAndButtonsAreShown() {
        composeTestRule
            .onNodeWithTag("Tutorial screen layout")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("Go forward button")
            .assertIsDisplayed()
            // Perform click to go to next screen and display the back button
            .performClick()

        composeTestRule
            .onNodeWithTag("Go back button")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("Skip button")
            .assertIsDisplayed()
    }

    @Test
    fun clickingOnForwardAndBackButtonsChangesThePage() {
        composeTestRule
            .onNodeWithTag("Current page is 0")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("Go forward button")
            .assertHasClickAction()
            .performClick()

        composeTestRule
            .onNodeWithTag("Current page is 1")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("Go forward button")
            .assertHasClickAction()
            .performClick()

        composeTestRule
            .onNodeWithTag("Current page is 2")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("Go back button")
            .assertHasClickAction()
            .performClick()

        composeTestRule
            .onNodeWithTag("Current page is 1")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("Go back button")
            .assertHasClickAction()
            .performClick()

        composeTestRule
            .onNodeWithTag("Current page is 0")
            .assertIsDisplayed()
    }

    @Test
    fun checkSlideContentIsCorrectlyDisplayed() {
        composeTestRule
            .onNodeWithTag("Tutorial Image")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("Tutorial Title")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("Tutorial Description")
            .assertIsDisplayed()
    }
}