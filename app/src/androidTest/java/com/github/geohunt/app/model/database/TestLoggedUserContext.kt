package com.github.geohunt.app.model.database

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.geohunt.app.authentication.Authenticator
import com.github.geohunt.app.linkCompletionTo
import com.github.geohunt.app.mocks.MockAuthenticator
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.model.database.firebase.FirebaseDatabase
import com.github.geohunt.app.utility.findActivity
import com.github.geohunt.app.utility.toCompletableFuture
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class TestLoggedUserContext {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var database: FirebaseDatabase
    private lateinit var loggedUser : User

    @Before
    fun setup() {
        runBlocking {
            FirebaseEmulator.init()
            composeTestRule.setContent {
                database = FirebaseDatabase(LocalContext.current.findActivity())
            }

            val user = database.getUserById("2").fetch()
                .await()

            composeTestRule.awaitIdle()
            Authenticator.authInstance.set(MockAuthenticator(user))
        }
    }

    @Test
    fun testIsLoggedUserCorrectlyDetectCurrentUser() = runTest {
        val futureUser1 = CompletableDeferred<User>()
        val futureUser2 = CompletableDeferred<User>()

        database.logged {
            assertThat(database.getUserById("2").isLoggedUser, equalTo(true))
            assertThat(database.getUserById("1").isLoggedUser, equalTo(false))

            database.getUserById("2").fetch()
                .toCompletableFuture()
                .linkCompletionTo(futureUser1)

            database.getUserById("1").fetch()
                .toCompletableFuture()
                .linkCompletionTo(futureUser2)
        }

        val user1 = futureUser1.await()
        val user2 = futureUser2.await()

        database.logged {
            assertThat(user1.isLoggedUser, equalTo(true))
            assertThat(user2.isLoggedUser, equalTo(false))
        }
    }

    @Test
    fun testLoggedUserMatchesLoggedUserRef() = database.logged {
        assertThat(loggedUser.id, equalTo("2"))
    }

}