package com.github.geohunt.app.model.database

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.geohunt.app.authentication.Authenticator
import com.github.geohunt.app.mocks.MockAuthenticator
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.model.database.api.LoggedUserContext
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.model.database.firebase.FirebaseDatabase
import com.github.geohunt.app.utility.findActivity
import com.github.geohunt.app.utility.thenMap
import com.github.geohunt.app.utility.toMap
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TestFirebaseActiveHunts {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val fakeLocation = Location(0.0, 0.0)

    private lateinit var database: FirebaseDatabase
    private lateinit var userRef: DatabaseReference
    private lateinit var challengeRef : DatabaseReference
    private lateinit var cid : String
    private lateinit var challenge : Challenge
    private lateinit var loggedUser : User

    private suspend fun getActiveHuntsList() =
        userRef.child("activeHunts").get().thenMap { it.toMap<Boolean>() }.await()

    private suspend fun getCounter() =
        challengeRef.child("numberOfActiveHunters").get().await().value as Long

    @Before
    fun setup() {
        runBlocking {
            FirebaseEmulator.init()
            composeTestRule.setContent {
                database = FirebaseDatabase(LocalContext.current.findActivity())
            }

            composeTestRule.awaitIdle()

            userRef = database.dbUserRef.push()
            challengeRef = database.dbChallengeRef.child(fakeLocation.getCoarseHash()).push()
            cid = fakeLocation.getCoarseHash() + challengeRef.key!!

            val mockUser = mapOf("followers" to 0,
                "followList" to emptyMap<String, Boolean>())
            userRef.setValue(mockUser).await()

            val mockChallenge = mapOf("authorId" to "0",
                "location" to mapOf("latitude" to 0, "longitude" to 1),
                "publishedDate" to "2023-04-08T15:43:41.648489Z",
                "numberOfActiveHunters" to 0)
            challengeRef.setValue(mockChallenge).await()

            loggedUser = database.getUserById(userRef.key!!).fetch().await()
            challenge = database.getChallengeById(cid).fetch().await()
        }
    }

    @Test
    fun testJoinChallengeUpdateFollowsList() = runTest {
        logged {
            challenge.joinHunt().await()

            assertThat(getActiveHuntsList(), hasEntry(cid, true))
        }
    }

    @Test
    fun testJoinChallengeUpdateCounter() = runTest {
        logged {
            challenge.joinHunt().await()

            assertThat(getCounter(), equalTo(1))
        }
    }

    @Test
    fun testLeaveChallengeUpdateFollowsList() = runTest {
        logged {
            challenge.joinHunt().await()
            challenge.leaveHunt().await()

            assertThat(getActiveHuntsList(), not(hasEntry(cid, true)))
        }
    }

    @Test
    fun testLeaveChallengeUpdateCounter() = runTest {
        logged {
            challenge.joinHunt().await()
            challenge.leaveHunt().await()

            assertThat(getCounter(), equalTo(0))
        }
    }

    private suspend fun logged(callback: suspend LoggedUserContext.() -> Unit) {
        Authenticator.authInstance.mocked(MockAuthenticator(loggedUser)).use {
            database.getLoggedContext().callback()
        }
    }
}
