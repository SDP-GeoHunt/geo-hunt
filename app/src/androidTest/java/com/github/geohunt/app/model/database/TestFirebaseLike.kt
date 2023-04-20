package com.github.geohunt.app.model.database

import android.graphics.Bitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.geohunt.app.authentication.Authenticator
import com.github.geohunt.app.mocks.MockAuthenticator
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.model.database.api.LoggedUserContext
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.model.database.firebase.FirebaseDatabase
import com.github.geohunt.app.model.database.firebase.getChallengeRefFromId
import com.github.geohunt.app.utility.findActivity
import com.github.geohunt.app.utility.thenMap
import com.github.geohunt.app.utility.toMap
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import okhttp3.internal.wait
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TestFirebaseLike {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var database: FirebaseDatabase
    private lateinit var userRef: DatabaseReference
    private lateinit var challengeRef: DatabaseReference
    private lateinit var loggedUser : User
    private lateinit var challenge : Challenge

    private suspend fun getUserLikeList() =
        userRef.child("likes").get().thenMap { snapshot -> snapshot.toMap<Boolean>() }.await()

    private suspend fun getChallengeLikeList() =
        challengeRef.child("likedBy").get().thenMap { snapshot -> snapshot.toMap<Boolean>() }.await()

    @Before
    fun setup() {
        runBlocking {
            FirebaseEmulator.init()

            composeTestRule.setContent {
                database = FirebaseDatabase(LocalContext.current.findActivity())
            }

            composeTestRule.awaitIdle()

            userRef = database.dbUserRef.push()

            val mockUser = mapOf("numberOfLikes" to 0, "likes" to emptyMap<String, Boolean>())
            userRef.setValue(mockUser).await()
            loggedUser = database.getUserById(userRef.key!!).fetch().await()

            logged {
                challenge = createChallenge(
                    Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888),
                    Location(43.1, 8.6),
                    difficulty = Challenge.Difficulty.MEDIUM
                ).await()
            }

            challengeRef = database.dbChallengeRef.getChallengeRefFromId(challenge.cid)
        }
    }

    @Test
    fun testChallengeLikeUpdatesLikeList() = runTest {
        logged {
            challenge.like().await()
        }

        assertThat(getUserLikeList(), hasEntry(challenge.cid, true))
    }

    @Test
    fun testLikeUpdatesLikeList() = runTest {
        logged {
            challenge.like().await()
        }

        assertThat(getChallengeLikeList(), hasEntry(userRef.key!!, true))
    }

    @Test
    fun testMultipleLikesIsNoOp() = runTest {
        logged {
            challenge.like().await()
            challenge.like().await()
            challenge.like().await()
        }

        assertThat(getUserLikeList(), not(hasEntry(not(challenge.cid), true)))
        assertThat(getChallengeLikeList(), not(hasEntry(not(userRef.key!!), true)))
    }

    @Test
    fun testUnlikeRemovesUserFromFollowList() = runTest {
        logged {
            challenge.like()
            challenge.unlike()
        }

        assertThat(getUserLikeList(), not(hasEntry(challenge.cid, true)))
    }

    @Test
    fun testUnlikeRemovesLikeFromChallenge() = runTest {
        logged {
            challenge.like()
            challenge.unlike()
        }

        assertThat(getChallengeLikeList(), not(hasEntry(userRef.key!!, true)))
    }

    @Test
    fun testMultipleUnlikesIsNoOp() = runTest {
        logged {
            challenge.like()
            challenge.unlike()
            challenge.unlike()
            challenge.unlike()
        }

        assertThat(getUserLikeList(), not(hasEntry(not(challenge.cid), true)))
        assertThat(getChallengeLikeList(), not(hasEntry(not(userRef.key!!), true)))
    }

    private suspend fun logged(callback: suspend LoggedUserContext.() -> Unit) {
        Authenticator.authInstance.mocked(MockAuthenticator(loggedUser)).use {
            database.getLoggedContext().callback()
        }
    }
}