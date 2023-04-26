package com.github.geohunt.app.model.database

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.geohunt.app.model.database.firebase.FirebaseDatabase
import com.github.geohunt.app.utility.findActivity
import com.github.geohunt.app.utility.thenMap
import com.github.geohunt.app.utility.toMap
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
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

    private suspend fun getUserLikeList() =
        userRef.child("likes").get().thenMap { snapshot -> snapshot.toMap<Boolean>() }.await()
    private suspend fun getChallengeLikeList() =
        database.getLikesOf(challengeRef.key!!).await()

    @Before
    fun setup() {
        runBlocking {
            FirebaseEmulator.init()
            composeTestRule.setContent {
                database = FirebaseDatabase(LocalContext.current.findActivity())
            }

            composeTestRule.awaitIdle()

            userRef = database.dbUserRef.push()
            challengeRef = database.dbChallengeRef.push()

            val mockUser = mapOf("numberOfLikes" to 0, "likes" to emptyMap<String, Boolean>())
            val mockChallenge = mapOf("numberOfLikes" to 0, "likedBy" to emptyMap<String, Boolean>())

            userRef.setValue(mockUser).await()
            challengeRef.setValue(mockChallenge).await()
        }
    }

    @Test
    fun testChallengeLikeUpdatesLikeList() = runTest {
        database.insertUserLike(userRef.key!!, challengeRef.key!!)

        assertThat(getUserLikeList(), hasEntry(challengeRef.key!!, true))
    }

    @Test
    fun testLikeUpdatesLikeList() = runTest {
        database.insertUserLike(userRef.key!!, challengeRef.key!!)

        assertThat(getChallengeLikeList(), hasEntry(userRef.key!!, true))
    }

    @Test
    fun testMultipleLikesIsNoOp() = runTest {
        database.insertUserLike(userRef.key!!, challengeRef.key!!)
        database.insertUserLike(userRef.key!!, challengeRef.key!!)
        database.insertUserLike(userRef.key!!, challengeRef.key!!)

        assertThat(getUserLikeList(), not(hasEntry(not(challengeRef.key!!), true)))
        assertThat(getChallengeLikeList(), not(hasEntry(not(userRef.key!!), true)))
    }

    @Test
    fun testUnlikeRemovesUserFromFollowList() = runTest {
        database.insertUserLike(userRef.key!!, challengeRef.key!!)
        database.removeUserLike(userRef.key!!, challengeRef.key!!)

        assertThat(getUserLikeList(), not(hasEntry(challengeRef.key!!, true)))
    }

    @Test
    fun testUnlikeDecrementsCounter() = runTest {
        database.insertUserLike(userRef.key!!, challengeRef.key!!)
        database.removeUserLike(userRef.key!!, challengeRef.key!!)
    }

    @Test
    fun testUnlikeRemovesLikeFromChallenge() = runTest {
        database.insertUserLike(userRef.key!!, challengeRef.key!!)
        database.removeUserLike(userRef.key!!, challengeRef.key!!)

        assertThat(getChallengeLikeList(), not(hasEntry(userRef.key!!, true)))
    }

    @Test
    fun testMultipleUnlikesIsNoOp() = runTest {
        database.insertUserLike(userRef.key!!, challengeRef.key!!)
        database.removeUserLike(userRef.key!!, challengeRef.key!!)
        database.removeUserLike(userRef.key!!, challengeRef.key!!)
        database.removeUserLike(userRef.key!!, challengeRef.key!!)

        assertThat(getUserLikeList(), not(hasEntry(not(challengeRef.key!!), true)))
        assertThat(getChallengeLikeList(), not(hasEntry(not(userRef.key!!), true)))
    }
}