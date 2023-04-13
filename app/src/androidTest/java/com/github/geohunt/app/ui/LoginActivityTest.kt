package com.github.geohunt.app.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.launchActivity
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.assertNoUnverifiedIntents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.geohunt.app.LoginActivity
import com.github.geohunt.app.MainActivity
import com.github.geohunt.app.authentication.Authenticator
import com.github.geohunt.app.mocks.MockAuthenticator
import com.github.geohunt.app.mocks.MockUser
import com.github.geohunt.app.model.database.api.User
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CompletableFuture

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {
    @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun opensHomeActivityWhenLoggedIn() {
        Authenticator.authInstance.set(MockAuthenticator(MockUser("hello")))

        Intents.init()
        launchActivity<LoginActivity>()
        intended(allOf(hasComponent(MainActivity::class.java.name)))
        Intents.release()
    }

    @Test
    fun doesNothingIfNotSignedIn() {
        Authenticator.authInstance.set(MockAuthenticator(null))
        Intents.init()

        launchActivity<LoginActivity>()
        intended(allOf(hasComponent(LoginActivity::class.java.name)))
        assertNoUnverifiedIntents()
        Intents.release()
    }

    @Test
    fun titleOfAppIsShown() {
        Authenticator.authInstance.set(MockAuthenticator(null))
        launchActivity<LoginActivity>()
        composeTestRule.onNodeWithText("GeoHunt").assertExists("Title of app does not appear on log in")
    }

    @Test
    fun clickingOnButtonTriggersSignIn() {
        val cf = CompletableFuture<Void>()
        Authenticator.authInstance.set(MockAuthenticator(null) {
            cf.complete(null)
            return@MockAuthenticator CompletableFuture.completedFuture(null)
        })
        Intents.init()

        launchActivity<LoginActivity>()
        composeTestRule.onNode(hasTestTag("signin-btn")).performClick()
        intended(allOf(
            hasComponent(LoginActivity::class.java.name),
            hasExtra("login", any(Any::class.java))))
        Intents.release()
        assert(cf.isDone)
    }
}