package com.github.geohunt.app.ui.components.profile.edit

import androidx.compose.runtime.MutableState
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.github.geohunt.app.mocks.BaseMockDatabase
import com.github.geohunt.app.mocks.MockLoggedUserContext
import com.github.geohunt.app.mocks.MockUser
import com.github.geohunt.app.model.database.api.EditedUser
import com.github.geohunt.app.model.database.api.LoggedUserContext
import com.github.geohunt.app.ui.Logged
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class EditProfileContentTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun changingNameCorrectlyUpdatesDatabase() {
        val cf = CompletableFuture<Void?>()
        val mockDb = object: BaseMockDatabase() {
            override fun getLoggedContext(): LoggedUserContext {
                return object : MockLoggedUserContext() {
                    override fun updateLoggedUser(editedUser: EditedUser): Task<Nothing?> {
                        assert(editedUser.displayName == "new display name")
                        cf.complete(null)
                        return Tasks.forResult(null)
                    }
                }
            }
        }

        val mockUser = MockUser(displayName = "caca")
        var instrumentableProfileEdit: MutableState<EditedUser>? = null

        composeTestRule.setContent {
            mockDb.Logged {
                instrumentableProfileEdit = instrumentableEditProfileContent(mockUser)
            }
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
            override fun getLoggedContext(): LoggedUserContext {
                return object : MockLoggedUserContext() {
                    override fun updateLoggedUser(editedUser: EditedUser): Task<Nothing?> {
                        cf.complete(null)
                        return Tasks.forException(Exception("Ok"))
                    }
                }
            }
        }

        val mockUser = MockUser(displayName = "caca")
        var instrumentableProfileEdit: MutableState<EditedUser>? = null

        composeTestRule.setContent {
            mockDb.Logged {
                instrumentableProfileEdit = instrumentableEditProfileContent(user = mockUser)
            }
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