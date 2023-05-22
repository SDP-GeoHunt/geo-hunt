package com.github.geohunt.app.ui.components.appbar

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class TopAppBarWithBackButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showsTitle() {
        composeTestRule.setContent {
            TopAppBarWithBackButton(onBack = {}, title = "A sample title")
        }
        composeTestRule.onNodeWithText("A sample title").assertIsDisplayed()
    }
}