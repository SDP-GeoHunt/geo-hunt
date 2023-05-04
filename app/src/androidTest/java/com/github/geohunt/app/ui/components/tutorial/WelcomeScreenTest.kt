package com.github.geohunt.app.ui.components.tutorial

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WelcomeScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val PREFERENCES_FILE = "preferences"

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val settings = context.getSharedPreferences(PREFERENCES_FILE, 0)

        settings.edit().putBoolean("first_application_open", true).apply()
    }

    @After
    fun teardown() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val settings = context.getSharedPreferences(PREFERENCES_FILE, 0)

        settings.edit().putBoolean("first_application_open", false).apply()
    }

    @Test
    fun opensWelcomeScreenWhenLoggedInForTheFirstTime() {
        composeTestRule
            .setContent {
                val shouldShowTutorial = remember { mutableStateOf(false) }
                WelcomeScreen(shouldShowTutorial = shouldShowTutorial)
            }

        composeTestRule
            .onNodeWithTag("Welcome Label")
            .assertExists()

        composeTestRule
            .onNodeWithTag("Welcome Description")
            .assertExists()

        composeTestRule
            .onNodeWithText("GET STARTED")
            .assertExists()
            .assertHasClickAction()
    }
}
