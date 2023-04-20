package com.github.geohunt.app.ui.components.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

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

    @Test
    fun backButtonWorks() {
        val cf = CompletableFuture<Void?>()
        composeTestRule.setContent {
            TopBarWithBackButton(onBack = { cf.complete(null) }, title = ".")
        }
        composeTestRule.onNodeWithTag("back-btn").performClick()

        cf.get(2, TimeUnit.SECONDS)
    }

}