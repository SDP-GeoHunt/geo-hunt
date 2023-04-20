package com.github.geohunt.app.ui.components.profile.edit

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.geohunt.app.authentication.Authenticator
import com.github.geohunt.app.mocks.BaseMockDatabase
import com.github.geohunt.app.mocks.InstantLazyRef
import com.github.geohunt.app.mocks.MockUser
import com.github.geohunt.app.model.LiveLazyRef
import com.github.geohunt.app.model.database.Database
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.ui.LoginActivityTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class ProfileEditPageTest {
    @get:Rule
    val c = createComposeRule()

    @Before
    fun mockAuthenticator() {
        Authenticator.authInstance.set(LoginActivityTest.MockAuthenticator(MockUser(uid = "1")))
    }

    @Test
    fun showsLoadingIfNotAvailableYet() {
        val mockDb = object: BaseMockDatabase() {
            override fun getUserById(uid: String): LiveLazyRef<User> {
                return InstantLazyRef("1", null)
            }
        }
        Database.databaseFactory.set { mockDb }

        c.setContent {
            ProfileEditPage { }
        }
        c.onNodeWithTag("progress").assertIsDisplayed()
    }

    @Test
    fun doesNotShowLoadingIfAvailable() {
        val mockDb = object: BaseMockDatabase() {
            override fun getUserById(uid: String): LiveLazyRef<User> {
                return InstantLazyRef("1", MockUser(uid = "1"))
            }
        }
        Database.databaseFactory.set { mockDb }
        c.setContent {
            ProfileEditPage { }
        }
        c.onNodeWithTag("progress").assertDoesNotExist()
    }

    @Test
    fun titleIsShown() {
        c.setContent { ProfileEditPage { } }
        c.onNodeWithText("Edit profile").assertIsDisplayed()
    }

    @Test
    fun clickingOnBackButtonTriggersCallback() {
        val cf = CompletableFuture<Void?>()
        c.setContent { ProfileEditPage {
            cf.complete(null)
        }}
        c.onNodeWithTag("back-btn").performClick()
        cf.get(2, TimeUnit.SECONDS)
    }
}