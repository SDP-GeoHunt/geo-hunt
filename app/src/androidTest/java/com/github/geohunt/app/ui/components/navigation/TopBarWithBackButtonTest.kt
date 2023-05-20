package com.github.geohunt.app.ui.components.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class TopBarWithBackButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showsTitle() {
        composeTestRule.setContent {
            TopBarWithBackButton(onBack = {}, title = "A sample title")
        }
        composeTestRule.onNodeWithText("A sample title").assertIsDisplayed()
    }
}