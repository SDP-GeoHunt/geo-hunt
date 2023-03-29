package com.github.geohunt.app.model.database

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.geohunt.app.model.database.firebase.FirebaseDatabase
import com.github.geohunt.app.utility.findActivity
import com.github.geohunt.app.utility.thenMap
import com.github.geohunt.app.utility.toMap
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    private suspend fun followList() =
        userRef1.child("followList").get().thenMap { snapshot -> snapshot.toMap<Boolean>() }.await()
    private suspend fun counter() =
        userRef2.child("followers").get().await().value as Int
    private suspend fun followers() =
        database.getFollowersOf(userRef2.key!!).await()

    @Before
    suspend fun setup() {
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
    }

    @Test
    fun testFollowsUpdatesFollowList() = runTest {
        database.follow(userRef1.key!!, userRef2.key!!)

        assertThat(followList(), hasEntry(userRef2.key!!, true))
    }

    @Test
    fun testFollowsIncrementCounter() = runTest {
        database.follow(userRef1.key!!, userRef2.key!!)

        assertThat(counter(), `is`(1))
    }

    @Test
    fun testFollowsUpdatesFollowerList() = runTest {
        database.follow(userRef1.key!!, userRef2.key!!)

        assertThat(followers(), hasEntry(userRef1.key!!, true))
    }

    @Test
    fun testMultipleFollowIsNoOp() = runTest {
        database.follow(userRef1.key!!, userRef2.key!!)
        database.follow(userRef1.key!!, userRef2.key!!)
        database.follow(userRef1.key!!, userRef2.key!!)

        // Assert that there is only one entry in each list
        assertThat(followList(), not(hasEntry(not(userRef2.key!!), true)))
        assertThat(counter(), `is`(1))
        assertThat(followers(), not(hasEntry(not(userRef1.key!!), true)))
    }

    @Test
    fun testUnfollowRemovesUserFromFollowList() = runTest {
        database.follow(userRef1.key!!, userRef2.key!!)
        database.unfollow(userRef1.key!!, userRef2.key!!)

        assertThat(followList(), not(hasEntry(userRef2.key!!, true)))
    }

    @Test
    fun testUnfollowDecrementCounter() = runTest {
        database.follow(userRef1.key!!, userRef2.key!!)
        database.unfollow(userRef1.key!!, userRef2.key!!)

        assertThat(counter(), `is`(0))
    }

    @Test
    fun testUnfollowRemovesFollower() = runTest {
        database.follow(userRef1.key!!, userRef2.key!!)
        database.unfollow(userRef1.key!!, userRef2.key!!)

        assertThat(followers(), not(hasEntry(userRef1.key!!, true)))
    }

    @Test
    fun testMultipleUnfollowIsNoOp() = runTest {
        database.follow(userRef1.key!!, userRef2.key!!)
        database.unfollow(userRef1.key!!, userRef2.key!!)
        database.unfollow(userRef1.key!!, userRef2.key!!)
        database.unfollow(userRef1.key!!, userRef2.key!!)

        assertThat(followList(), not(hasValue(true)))
        assertThat(counter(), `is`(0))
        assertThat(followers(), not(hasValue(true)))
    }
}