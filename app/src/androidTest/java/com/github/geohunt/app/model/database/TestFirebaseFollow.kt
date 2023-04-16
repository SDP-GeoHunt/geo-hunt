package com.github.geohunt.app.model.database

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.geohunt.app.authentication.Authenticator
import com.github.geohunt.app.linkCompletionTo
import com.github.geohunt.app.mocks.MockAuthenticator
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.LoggedUserContext
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.model.database.firebase.FirebaseDatabase
import com.github.geohunt.app.utility.*
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TestFirebaseFollow {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var database: FirebaseDatabase
    private lateinit var userRef1: DatabaseReference
    private lateinit var userRef2: DatabaseReference
    private lateinit var otherUserRef : LazyRef<User>
    private lateinit var otherUser : User
    private lateinit var loggedUser : User

    private suspend fun getFollowList() =
        userRef1.child("followList").get().thenMap { snapshot -> snapshot.toMap<Boolean>() }.await()
    private suspend fun getCounter() =
        userRef2.child("numberOfFollowers").get().await().value as Long
    private suspend fun getFollowers2() =
        database.getFollowersOf(userRef2.key!!).await()

    @Before
    fun setup() {
        runBlocking {
            FirebaseEmulator.init()
            composeTestRule.setContent {
                database = FirebaseDatabase(LocalContext.current.findActivity())
            }

            composeTestRule.awaitIdle()

            userRef1 = database.dbUserRef.push()
            userRef2 = database.dbUserRef.push()

            val mockUser = mapOf("followers" to 0, "followList" to emptyMap<String, Boolean>())

            userRef1.setValue(mockUser).await()
            userRef2.setValue(mockUser).await()
            otherUserRef = database.getUserById(userRef2.key!!)

            otherUser = otherUserRef.fetch().await()
            loggedUser = database.getUserById(userRef1.key!!).fetch().await()
        }
    }

    @Test
    fun testFollowsUpdatesFollowList() = runTest {
        logged {
            val future = CompletableDeferred<Nothing?>()
            val resultDoesFollow = CompletableDeferred<Boolean>()

            database.logged {
                otherUserRef.follow()
                    .linkCompletionTo(future)
            }

            future.await()
            assertThat(getFollowList(), hasEntry(userRef2.key!!, true))

            otherUser.doesFollow.fetch().linkCompletionTo(resultDoesFollow)

            assertThat(resultDoesFollow.await(), equalTo(true))
        }
    }

    @Test
    fun testFollowsIncrementCounter() = runTest {
        logged {
            val future = CompletableDeferred<Nothing?>()

            otherUserRef.follow().linkCompletionTo(future)

            future.await()
            assertThat(getCounter(), `is`(1))
        }
    }

    @Test
    fun testFollowsUpdatesFollowerList() = runTest {
        logged {
            val future = CompletableDeferred<Nothing?>()

            otherUserRef.follow().linkCompletionTo(future)

            future.await()
            assertThat(getFollowers2().map { it.id }, hasItem(userRef1.key!!))
        }
    }

    @Test
    fun testMultipleFollowIsNoOp() = runTest {
        logged {
            val future = CompletableDeferred<Nothing?>()
            val resultDoesFollow = CompletableDeferred<Boolean>()

            otherUser.follow()
                .thenDo { otherUser.follow() }
                .thenDo { otherUser.follow() }
                .linkCompletionTo(future)

            // Assert that there is only one entry in each list
            future.await()
            assertThat(getFollowList(), not(hasEntry(not(userRef2.key!!), true)))
            assertThat(getCounter(), `is`(1))
            assertThat(getFollowers2().map { it.id }, not(hasItem(not(userRef1.key!!))))

            otherUser.doesFollow.fetch().linkCompletionTo(resultDoesFollow)

            assertThat(resultDoesFollow.await(), equalTo(true))
        }
    }

    @Test
    fun testUnfollowRemovesUserFromFollowList() = runTest {
        logged {
            val future = CompletableDeferred<Nothing?>()
            val resultDoesFollow = CompletableDeferred<Boolean>()

            otherUser.follow()
                .thenDo { otherUser.follow() }
                .thenDo { otherUser.unfollow() }
                .linkCompletionTo(future)

            future.await()
            assertThat(getFollowList(), not(hasEntry(userRef2.key!!, true)))

            otherUser.doesFollow.fetch().linkCompletionTo(resultDoesFollow)
            assertThat(resultDoesFollow.await(), equalTo(false))
        }
    }

    @Test
    fun testUnfollowDecrementCounter() = runTest {
        logged {
            val future = CompletableDeferred<Nothing?>()

            otherUser.follow()
                .thenDo { otherUser.unfollow() }
                .linkCompletionTo(future)

            future.await()
            assertThat(getCounter(), `is`(0))
        }
    }

    @Test
    fun testUnfollowRemovesFollower() = runTest {
        logged {
            val future = CompletableDeferred<Nothing?>()

            otherUser.follow()
                .thenDo { otherUser.unfollow() }
                .linkCompletionTo(future)

            future.await()
            assertThat(getFollowers2().map { it.id }, not(hasItem(userRef1.key!!)))
        }
    }

    @Test
    fun testMultipleUnfollowIsNoOp() = runTest {
        logged {
            val future = CompletableDeferred<Nothing?>()

            otherUser.follow()
                .thenDo { otherUser.unfollow() }
                .thenDo { otherUser.unfollow() }
                .thenDo { otherUser.unfollow() }
                .linkCompletionTo(future)

            future.await()
            assertThat(getFollowList(), not(hasValue(true)))
            assertThat(getCounter(), `is`(0))
            assertThat(getFollowers2(), emptyIterable())
        }
    }

    private suspend fun logged(callback: suspend LoggedUserContext.() -> Unit) {
        Authenticator.authInstance.mocked(MockAuthenticator(loggedUser)).use {
            database.getLoggedContext().callback()
        }
    }
}