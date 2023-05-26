package com.github.geohunt.app.ui.components.appbar

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.*
import org.junit.Rule

import org.junit.Test

class MainAppBarTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun titleIsDisplayed() {
        composeTestRule.setContent {
            MainAppBar(title = "Mock title", openDrawer = {})
        }

        composeTestRule.onNodeWithText("Mock title").assertIsDisplayed()
    }

    @Test
    fun drawerIsOpenedOnClick() {
        var isOpened = false

        composeTestRule.setContent {
            MainAppBar(title = "Mock title", openDrawer = { isOpened = true })
        }

        val menuButton = composeTestRule.onNodeWithTag("mainAppBar-nav")
        menuButton.assertIsDisplayed()
        menuButton.assertHasClickAction()
        menuButton.performClick()

        assertTrue(isOpened)
    }
}