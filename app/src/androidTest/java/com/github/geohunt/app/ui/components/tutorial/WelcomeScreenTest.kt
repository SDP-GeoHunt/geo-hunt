package com.github.geohunt.app.ui.components.tutorial

import android.content.SharedPreferences
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.launchActivity
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.geohunt.app.LoginActivity
import com.github.geohunt.app.MainActivity
import com.github.geohunt.app.TutorialActivity
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WelcomeScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val PREFERENCES_FILE = "preferences"

    @Before
    fun setup(){
        val settings: SharedPreferences = composeTestRule.activity.getSharedPreferences(PREFERENCES_FILE, 0)
        settings.edit().putBoolean("first_application_open", true).apply()
    }

    @After
    fun tearDown(){
        val settings: SharedPreferences = composeTestRule.activity.getSharedPreferences(PREFERENCES_FILE, 0)
        settings.edit().putBoolean("first_application_open", false).apply()
    }

    @Test
    fun opensWelcomeScreenWhenLoggedInForTheFirstTime() {
        Intents.init()
        launchActivity<TutorialActivity>()

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

        Intents.release()
    }

    @Test
    fun clickingOnSkipButtonOpensLoginScreen() {
        Intents.init()
        launchActivity<TutorialActivity>()

        composeTestRule
            .onNodeWithText("GET STARTED")
            .performClick()

        composeTestRule
            .onNodeWithTag("Skip button")
            .assertHasClickAction()
            .performClick()

        Intents.intended(Matchers.allOf(IntentMatchers.hasComponent(LoginActivity::class.java.name)))
        Intents.release()
    }

    @Test
    fun clickingOnGetStartedButtonOpensTutorialSlides(){
        Intents.init()
        launchActivity<TutorialActivity>()

        composeTestRule.onNodeWithText("GET STARTED")
            .assertExists()
            .assertHasClickAction()
            .performClick()

        composeTestRule
            .onNodeWithTag("Tutorial screen layout")
            .assertExists()

        Intents.release()
    }
}