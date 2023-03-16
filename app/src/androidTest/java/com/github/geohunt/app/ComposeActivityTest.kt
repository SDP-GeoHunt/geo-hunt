package com.github.geohunt.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class ComposeActivityTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testMainComposeActivity() {
        // Start the application
        composeTestRule.setContent {
            DefaultPreview()
        }

        composeTestRule.onNodeWithText("Hello Android!")
                .assertIsDisplayed();
    }
}