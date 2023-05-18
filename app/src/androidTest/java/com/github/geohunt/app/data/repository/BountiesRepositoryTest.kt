package com.github.geohunt.app.data.repository

import androidx.test.espresso.matcher.ViewMatchers.assertThat
import com.github.geohunt.app.data.repository.bounties.BountiesRepository
import com.github.geohunt.app.mocks.MockAuthRepository
import com.github.geohunt.app.mocks.MockUserRepository
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.model.database.FirebaseEmulator
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class BountiesRepositoryTest {
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var repo: BountiesRepository

    val mockLocation = Location(33.047236, 86.971963)

    @Before
    fun setup() {
        database = FirebaseEmulator.getEmulatedFirebase()
        storage = FirebaseEmulator.getEmulatedStorage()
        repo = BountiesRepository(
            MockUserRepository(),
            MockAuthRepository(),
            ImageRepository(storage),
            database = database,
            storage = storage
        )
    }

    @Test
    fun createsProperlyABounty() = runTest {
        val bounty = repo.createBounty(
            "bounty_name",
            startingDate = LocalDateTime.now(),
            expirationDate = LocalDateTime.now().plusDays(2),
            location = mockLocation
        )

        val bounty2 = repo.getBountyById(bounty.bid)
        assertThat(bounty.name, equalTo("bounty_name"))
        assertThat(bounty.bid, equalTo(bounty2.bid))
        assertThat(bounty.location, equalTo(bounty2.location))
        assertThat(bounty.expirationDate, equalTo(bounty2.expirationDate))
        assertThat(bounty.startingDate, equalTo(bounty2.startingDate))
        assertThat(bounty.adminUid, equalTo(bounty2.adminUid))
        assertThat(bounty.adminUid, equalTo("1"))
    }

    @Test
    fun testBountyByUser() = runTest {
        val bounty = repo.createBounty(
            "bounty_name",
            startingDate = LocalDateTime.now(),
            expirationDate = LocalDateTime.now().plusDays(2),
            location = mockLocation
        )

        val bounties = repo.getBountyCreatedBy(MockUserRepository().getCurrentUser())

        assertThat(bounties.map{ it.bid }, hasItem(bounty.bid))
    }
}