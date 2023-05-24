package com.github.geohunt.app.data.repository

import com.github.geohunt.app.mocks.MockAuthRepository
import com.github.geohunt.app.mocks.MockChallenge
import com.github.geohunt.app.model.database.FirebaseEmulator
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test

class ActiveHuntsRepositoryTest {
    private lateinit var database: FirebaseDatabase
    private val auth = MockAuthRepository()
    @OptIn(ExperimentalCoroutinesApi::class)
    private val ioDispatcher = UnconfinedTestDispatcher()

    private val mockChallenge1 = MockChallenge("test1")
    private val mockChallenge2 = MockChallenge("test2")
    private val mockChallenge3 = MockChallenge("test3")

    @Before
    fun setupEmulator() {
        database = FirebaseEmulator.getEmulatedFirebase()
    }

    @After
    fun deleteScores() {
        database.getReference("activeHunts").removeValue()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun joinHuntCorrectlyUpdatesDatabase() = runTest {
        val activeHunts = ActiveHuntsRepository(auth, database, ioDispatcher)

        activeHunts.joinHunt(mockChallenge1)
        activeHunts.joinHunt(mockChallenge2)

        val data = database.getReference("activeHunts").child("1").get().await()
        assertThat(data.childrenCount, equalTo(2L))
        val entries = data.children.map { it.key!! }
        assertThat(entries, containsInAnyOrder(mockChallenge1.id, mockChallenge2.id))

        activeHunts.joinHunt(mockChallenge3)
        val data2 = database.getReference("activeHunts").child("1").get().await()
        assertThat(data2.childrenCount, equalTo(3L))
        val entries2 = data2.children.map { it.key!! }
        assertThat(entries2, containsInAnyOrder(mockChallenge1.id, mockChallenge2.id, mockChallenge3.id))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun leaveCorrectlyUpdatesDatabase() = runTest {
            val activeHunts = ActiveHuntsRepository(auth, database, ioDispatcher)

            activeHunts.joinHunt(mockChallenge1)
            activeHunts.joinHunt(mockChallenge2)
            activeHunts.joinHunt(mockChallenge3)

            activeHunts.leaveHunt(mockChallenge3)

            val data = database.getReference("activeHunts").child("1").get().await()
            assertThat(data.childrenCount, equalTo(2L))
            val entries = data.children.map { it.key!! }
            assertThat(entries, containsInAnyOrder(mockChallenge1.id, mockChallenge2.id))

            activeHunts.leaveHunt(mockChallenge2)
            val data2 = database.getReference("activeHunts").child("1").get().await()
            assertThat(data2.childrenCount, equalTo(1L))
            val entries2 = data2.children.map { it.key!! }
            assertThat(entries2, containsInAnyOrder(mockChallenge1.id))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getHuntsReturnsEveryHunt() = runTest {
        val activeHunts = ActiveHuntsRepository(auth, database, ioDispatcher)

        activeHunts.joinHunt(mockChallenge1)
        activeHunts.joinHunt(mockChallenge2)
        activeHunts.joinHunt(mockChallenge3)

        val hunts = activeHunts.getActiveHunts().first()
        assertThat(hunts, containsInAnyOrder(mockChallenge1.id, mockChallenge2.id, mockChallenge3.id))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun isHuntingReturnsCorrectInfo() = runTest {
        val activeHunts = ActiveHuntsRepository(auth, database, ioDispatcher)

        activeHunts.joinHunt(mockChallenge1)

        assertThat(activeHunts.isHunting(mockChallenge1).first(), equalTo(true))
        assertThat(activeHunts.isHunting(mockChallenge3).first(), equalTo(false))

        val boolFlow = activeHunts.isHunting(mockChallenge2)
        val before = boolFlow.first()
        assertThat(before, equalTo(false))

        advanceUntilIdle()
        activeHunts.joinHunt(mockChallenge2)
        val after = boolFlow.first()
        assertThat(after, equalTo(true))
    }
}