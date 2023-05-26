package com.github.geohunt.app.data.repository

import com.firebase.ui.auth.IdpResponse
import com.github.geohunt.app.mocks.MockAuthRepository
import com.github.geohunt.app.mocks.mockUser
import com.github.geohunt.app.model.database.FirebaseEmulator
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class UserRepositoryTest {
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var image: ImageRepository
    private val ioDispatcher = UnconfinedTestDispatcher()

    private val user = mockUser("name1","dname1","url")
    private val auth = MockAuthRepository(user)
    private val trueIdpResponse = mock<IdpResponse> { on { isNewUser } doReturn true }


    @Before
    fun setupEmulator() {
        database = FirebaseEmulator.getEmulatedFirebase()
        storage = FirebaseEmulator.getEmulatedStorage()
        image = ImageRepository(storage, ioDispatcher)
    }

    @After
    fun deleteUsers() {
        database.getReference("users").child("name1").removeValue()
    }

    private fun users(): UserRepositoryInterface {
        return UserRepository(image, auth, database, ioDispatcher)
    }

    @Test
    fun getProfilePictureReturnsUrl() {
        val mockUser = mockUser(profilePictureUrl = "yes")
        Assert.assertEquals(users().getProfilePictureUrl(mockUser), mockUser.profilePictureUrl)
    }

    @Test
    fun createUserIfNewCreatesUser() = runTest {
        val users = users()
        users.createUserIfNew(trueIdpResponse)
        advanceUntilIdle()

        val createdUser = user

        val displayName = database.getReference("users")
            .child(createdUser.id)
            .child("displayName")
            .get().await()
            .getValue(String::class.java)
        Assert.assertEquals(createdUser.displayName, displayName)

        val id = database.getReference("users")
            .child(createdUser.id)
            .child("id")
            .get().await()
            .getValue(String::class.java)
        Assert.assertEquals(createdUser.id, id)

        val profileUrl = database.getReference("users")
            .child(createdUser.id)
            .child("profilePictureUrl")
            .get().await()
            .getValue(String::class.java)
        Assert.assertEquals(createdUser.profilePictureUrl, profileUrl)
    }


}