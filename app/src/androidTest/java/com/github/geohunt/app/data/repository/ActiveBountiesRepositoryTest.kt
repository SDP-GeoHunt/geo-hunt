package com.github.geohunt.app.data.repository

import com.github.geohunt.app.data.repository.bounties.ActiveBountiesRepository
import com.github.geohunt.app.mocks.MockAuthRepository
import com.github.geohunt.app.mocks.MockBounty
import com.github.geohunt.app.model.database.FirebaseEmulator
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ActiveBountiesRepositoryTest {
    private lateinit var database: FirebaseDatabase
    private val auth = MockAuthRepository()
    private val ioDispatcher = UnconfinedTestDispatcher()

    private val mockBounty1 = MockBounty("test1")
    private val mockBounty2 = MockBounty("test2")
    private val mockBounty3 = MockBounty("test3")

    @Before
    fun setupEmulator() {
        database = FirebaseEmulator.getEmulatedFirebase()
    }

    @After
    fun deleteScores() {
        database.getReference("bounties/ofUser").removeValue()
    }

    @Test
    fun joinBountyUpdatesDatabase() = runTest {
        val activeBounties = ActiveBountiesRepository(database, auth, ioDispatcher)

        activeBounties.joinBounty(mockBounty1.bid)
        activeBounties.joinBounty(mockBounty2.bid)

        val data = database.getReference("bounties/ofUser").child("1").get().await()
        assertThat(data.childrenCount, equalTo(2L))
        val entries = data.children.map { it.key!! }
        assertThat(entries, containsInAnyOrder(mockBounty2.bid, mockBounty1.bid))

        activeBounties.joinBounty(mockBounty3.bid)
        val data2 = database.getReference("bounties/ofUser").child("1").get().await()
        assertThat(data2.childrenCount, equalTo(2L))
        val entries2 = data2.children.map { it.key!! }
        assertThat(entries2, containsInAnyOrder(mockBounty2.bid, mockBounty1.bid, mockBounty3))
    }

    @Test
    fun leaveBountyUpdatesDatabase() = runTest {
        val activeBounties = ActiveBountiesRepository(database, auth, ioDispatcher)

        activeBounties.joinBounty(mockBounty1.bid)
        activeBounties.joinBounty(mockBounty2.bid)

        activeBounties.leaveBounty(mockBounty1.bid)
        val data = database.getReference("bounties/ofUser").child("1").get().await()
        assertThat(data.childrenCount, equalTo(1L))
        val entries = data.children.map { it.key!! }
        assertThat(entries, containsInAnyOrder(mockBounty2.bid))
    }

    @Test
    fun getBountiesCorrectlyRetrievesBounties() = runTest {
        val activeBounties = ActiveBountiesRepository(database, auth, ioDispatcher)

        activeBounties.joinBounty(mockBounty1.bid)
        activeBounties.joinBounty(mockBounty2.bid)

        val bounties = activeBounties.getBounties().first()
        assertThat(bounties, containsInAnyOrder(mockBounty2.bid, mockBounty1.bid))
    }
}