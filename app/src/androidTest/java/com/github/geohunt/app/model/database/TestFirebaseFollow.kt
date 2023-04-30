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
class TestFirebaseFollow {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var database: FirebaseDatabase
    private lateinit var userRef1: DatabaseReference
    private lateinit var userRef2: DatabaseReference

    private suspend fun getFollowList() =
        userRef1.child("followList").get().thenMap { snapshot -> snapshot.toMap<Boolean>() }.await()
    private suspend fun getCounter() =
        userRef2.child("numberOfFollowers").get().await().value as Long
    private suspend fun getFollowers() =
        database.getFollowersOf(userRef2.key!!).await().map { it.id }

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
        }
    }

    @Test
    fun testFollowsUpdatesFollowList() = runTest {
        database.follow(userRef1.key!!, userRef2.key!!)

        assertThat(getFollowList(), hasEntry(userRef2.key!!, true))
    }

    @Test
    fun testFollowsIncrementCounter() = runTest {
        database.follow(userRef1.key!!, userRef2.key!!)

        assertThat(getCounter(), `is`(1))
    }

    @Test
    fun testFollowsUpdatesFollowerList() = runTest {
        database.follow(userRef1.key!!, userRef2.key!!)

        assertThat(getFollowers(), hasItem(userRef1.key!!))
    }

    @Test
    fun testMultipleFollowIsNoOp() = runTest {
        database.follow(userRef1.key!!, userRef2.key!!)
        database.follow(userRef1.key!!, userRef2.key!!)
        database.follow(userRef1.key!!, userRef2.key!!)

        // Assert that there is only one entry in each list
        assertThat(getFollowList(), not(hasEntry(not(userRef2.key!!), true)))
        assertThat(getCounter(), `is`(1))
        assertThat(getFollowers(), not(hasItem(not(userRef1.key!!))))
    }

    @Test
    fun testUnfollowRemovesUserFromFollowList() = runTest {
        database.follow(userRef1.key!!, userRef2.key!!)
        database.unfollow(userRef1.key!!, userRef2.key!!)

        assertThat(getFollowList(), not(hasEntry(userRef2.key!!, true)))
    }

    @Test
    fun testUnfollowDecrementCounter() = runTest {
        database.follow(userRef1.key!!, userRef2.key!!)
        database.unfollow(userRef1.key!!, userRef2.key!!)

        assertThat(getCounter(), `is`(0))
    }

    @Test
    fun testUnfollowRemovesFollower() = runTest {
        database.follow(userRef1.key!!, userRef2.key!!)
        database.unfollow(userRef1.key!!, userRef2.key!!)

        assertThat(getFollowers(), not(hasItem(userRef1.key!!)))
    }

    @Test
    fun testMultipleUnfollowIsNoOp() = runTest {
        database.follow(userRef1.key!!, userRef2.key!!)
        database.unfollow(userRef1.key!!, userRef2.key!!)
        database.unfollow(userRef1.key!!, userRef2.key!!)
        database.unfollow(userRef1.key!!, userRef2.key!!)

        assertThat(getFollowList(), not(hasValue(true)))
        assertThat(getCounter(), `is`(0))
        assertThat(getFollowers(), emptyIterable())
    }
}