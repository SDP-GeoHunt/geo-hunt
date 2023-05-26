package com.github.geohunt.app.data.repository

import com.github.geohunt.app.mocks.mockUser
import com.github.geohunt.app.model.database.FirebaseEmulator
import com.github.geohunt.app.model.database.api.ProfileVisibility
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileVisibilityRepositoryTest {
    private lateinit var database: FirebaseDatabase
    private lateinit var profileVisibilityReference: DatabaseReference
    private val ioDispatcher = UnconfinedTestDispatcher()
    private val dummyUid = "visibility1"

    @Before
    fun setupEmulator() {
        database = FirebaseEmulator.getEmulatedFirebase()
        profileVisibilityReference = database.getReference("profileVisibilities")
    }

    @After
    fun clearEmulator() {
        profileVisibilityReference.removeValue()
    }

    private fun profileVisibility(): ProfileVisibilityRepositoryInterface {
        return ProfileVisibilityRepository(database, ioDispatcher)
    }

    @Test
    fun setProfileVisibilityUpdatesDatabase() = runTest {
        val visibility = profileVisibility()

        val newVisibility1 = ProfileVisibility.PRIVATE
        visibility.setProfileVisibility(dummyUid, newVisibility1)
        val dbVisibility1 = profileVisibilityReference.child(dummyUid).get().await().getValue(Integer::class.java)
        assertEquals(newVisibility1.ordinal, dbVisibility1)

        val newVisibility2 = ProfileVisibility.FOLLOWING_ONLY
        visibility.setProfileVisibility(mockUser(dummyUid), newVisibility2)
        val dbVisibility2 = profileVisibilityReference.child(dummyUid).get().await().getValue(Integer::class.java)
        assertEquals(newVisibility2.ordinal, dbVisibility2)
    }

    @Test
    fun getProfileVisibilityCorrectlyUpdates() = runTest {
        val visibility = profileVisibility()

        val mockUser = mockUser(dummyUid)

        val visibilityFlow1 = visibility.getProfileVisibility(mockUser)
        val visibilityFlow2 = visibility.getProfileVisibility(mockUser.id)

        assertEquals(visibilityFlow1.first(), ProfileVisibility.PUBLIC)
        assertEquals(visibilityFlow2.first(), ProfileVisibility.PUBLIC)

        visibility.setProfileVisibility(dummyUid, ProfileVisibility.FOLLOWING_ONLY)
        assertEquals(visibilityFlow1.first(), ProfileVisibility.FOLLOWING_ONLY)
        assertEquals(visibilityFlow2.first(), ProfileVisibility.FOLLOWING_ONLY)

        visibility.setProfileVisibility(dummyUid, ProfileVisibility.PRIVATE)
        assertEquals(visibilityFlow1.first(), ProfileVisibility.PRIVATE)
        assertEquals(visibilityFlow2.first(), ProfileVisibility.PRIVATE)
    }
}