package com.github.geohunt.app.ui

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.launchActivity
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.assertNoUnverifiedIntents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.geohunt.app.LoginActivity
import com.github.geohunt.app.MainActivity
import com.github.geohunt.app.mocks.MockAuthRepository
import com.github.geohunt.app.mocks.MockUserRepository
import com.github.geohunt.app.ui.screens.login.LoginViewModel
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {
    @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

    @After
    fun uninject() {
        LoginViewModel.uninjectRepos()
    }

    @Test
    fun opensHomeActivityWhenLoggedIn() {
        LoginViewModel.injectRepos(MockAuthRepository(), MockUserRepository())

        Intents.init()
        launchActivity<LoginActivity>()
        intended(allOf(hasComponent(MainActivity::class.java.name)))
        Intents.release()
    }

    @Test
    fun doesNothingIfNotSignedIn() {
        LoginViewModel.injectRepos(MockAuthRepository(null), MockUserRepository())

        Intents.init()
        launchActivity<LoginActivity>()
        intended(allOf(hasComponent(LoginActivity::class.java.name)))
        assertNoUnverifiedIntents()
        Intents.release()
    }

    @Test
    fun titleOfAppIsShown() {
        LoginViewModel.injectRepos(MockAuthRepository(null), MockUserRepository())
        Intents.init()
        launchActivity<LoginActivity>()
        composeTestRule.onNodeWithText("GeoHunt").assertExists("Title of app does not appear on log in")
        Intents.release()
    }

    // fails, because it creates a supplementary activity.
    @Test
    fun clickingOnButtonTriggersSignIn() {
        LoginViewModel.injectRepos(MockAuthRepository(null), MockUserRepository())


        Intents.init()
        launchActivity<LoginActivity>()

        composeTestRule.onNode(hasTestTag("signin-btn")).performClick()

        intended(hasComponent("com.firebase.ui.auth.KickoffActivity"))

        Intents.release()
    }
}