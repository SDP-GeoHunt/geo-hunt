package com.github.geohunt.app.ui.components.profile.edit

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.github.geohunt.app.authentication.Authenticator
import com.github.geohunt.app.mocks.*
import com.github.geohunt.app.model.LiveLazyRef
import com.github.geohunt.app.model.LiveLazyRefListener
import com.github.geohunt.app.model.database.api.LoggedUserContext
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.ui.WithLoggedUserContext
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
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
        Authenticator.authInstance.set(MockAuthenticator(MockUser(uid = "1")))
    }

    @Test
    fun showsLoadingIfNotAvailableYet() {
        val mockDb = createMockDatabase(true)

        c.setContent {
            mockDb.WithLoggedUserContext {
                ProfileEditPage { }
            }
        }
        c.onNodeWithTag("progress").assertIsDisplayed()
    }

    @Test
    fun doesNotShowLoadingIfAvailable() {
        val mockDb = createMockDatabase()

        c.setContent {
            mockDb.WithLoggedUserContext {
                ProfileEditPage { }
            }
        }
        c.onNodeWithTag("progress").assertDoesNotExist()
    }

    @Test
    fun titleIsShown() {
        val mockDb = createMockDatabase()

        c.setContent {
            mockDb.WithLoggedUserContext {
                ProfileEditPage { }
            }
        }
        c.onNodeWithText("Edit profile").assertIsDisplayed()
    }

    @Test
    fun clickingOnBackButtonTriggersCallback() {
        val mockDb = createMockDatabase()

        val cf = CompletableFuture<Void?>()
        c.setContent {
            mockDb.WithLoggedUserContext {
                ProfileEditPage { cf.complete(null) }
            }
        }

        c.onNodeWithTag("back-btn").performClick()
        cf.get(2, TimeUnit.SECONDS)
    }


    private fun createMockDatabase(isLoadingUser : Boolean = false) = object : BaseMockDatabase() {
        override fun getUserById(uid: String): LiveLazyRef<User> {
            return InstantLazyRef("1", MockUser(uid = "1"))
        }

        override fun getLoggedContext(): LoggedUserContext {
            return object : MockLoggedUserContext() {
                override val loggedUserRef: LiveLazyRef<User>
                    get() {
                        return object : LiveLazyRef<User>() {
                            override fun addListener(callback: (User) -> Any?): LiveLazyRefListener {
                                TODO("Not yet implemented")
                            }

                            override fun fetchValue(): Task<User> {
                                return if (isLoadingUser) {
                                    val completionSource = TaskCompletionSource<User>()
                                    completionSource.task
                                }
                                else {
                                    Tasks.forResult(MockUser("1", uid = "1"))
                                }
                            }

                            override val id: String
                                get() = "1"
                        }
                    }
            }
        }
    }
}