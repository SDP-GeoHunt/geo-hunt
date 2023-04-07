package com.github.geohunt.app.ui.components.profile.edit

import androidx.compose.runtime.MutableState
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.github.geohunt.app.mocks.BaseMockDatabase
import com.github.geohunt.app.mocks.MockUser
import com.github.geohunt.app.model.database.Database
import com.github.geohunt.app.model.database.api.EditedUser
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import org.junit.Rule
import org.junit.Test
import java.lang.Exception
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class EditProfileContentTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun changingNameCorrectlyUpdatesDatabase() {
        val cf = CompletableFuture<Void?>()
        val mockDb = object: BaseMockDatabase() {
            override fun updateUser(user: EditedUser): Task<Void?> {
                assert(user.displayName == "new display name")
                cf.complete(null)
                return Tasks.forResult(null)
            }
        }

        Database.databaseFactory.set { return@set mockDb }
        val mockUser = MockUser(displayName = "caca")
        var instrumentableProfileEdit: MutableState<EditedUser>? = null

        composeTestRule.setContent {
            instrumentableProfileEdit = instrumentableEditProfileContent(user = mockUser)
        }

        assert(instrumentableProfileEdit != null)

        instrumentableProfileEdit?.value = instrumentableProfileEdit?.value!!.copy(displayName = "new display name")

        composeTestRule.onNodeWithTag("save-btn").performClick()
        cf.get(15, TimeUnit.SECONDS)
    }

    @Test
    fun gracefullyHandlesSavingExceptions() {
        val cf = CompletableFuture<Void?>()
        val mockDb = object: BaseMockDatabase() {
            override fun updateUser(user: EditedUser): Task<Void?> {
                cf.complete(null)
                return Tasks.forException(Exception("Ok"))
            }
        }

        Database.databaseFactory.set { return@set mockDb }
        val mockUser = MockUser(displayName = "caca")
        var instrumentableProfileEdit: MutableState<EditedUser>? = null

        composeTestRule.setContent {
            instrumentableProfileEdit = instrumentableEditProfileContent(user = mockUser)
        }

        assert(instrumentableProfileEdit != null)

        instrumentableProfileEdit?.value = instrumentableProfileEdit?.value!!.copy(displayName = "new display name")

        composeTestRule.onNodeWithTag("save-btn").performClick()
        cf.get(15, TimeUnit.SECONDS)
        // If the code did not handle exception, it'd hide the "save-btn" and enter a
        // locked state.
        composeTestRule.onNodeWithTag("save-btn").assertExists()
    }
}