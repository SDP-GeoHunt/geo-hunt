package com.github.geohunt.app.data.repository

import com.github.geohunt.app.mocks.MockAuthRepository
import com.github.geohunt.app.mocks.mockUser
import com.github.geohunt.app.model.database.FirebaseEmulator
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

@Suppress("DEPRECATION")
@OptIn(ExperimentalCoroutinesApi::class)
class FollowRepositoryTest {
    private lateinit var database: FirebaseDatabase
    private val ioDispatcher = UnconfinedTestDispatcher()
    private val auth = MockAuthRepository()
    private val currentUserId = auth.getCurrentUser().id

    @Before
    fun setupEmulator() {
        database = FirebaseEmulator.getEmulatedFirebase()
    }

    @After
    fun clearEmulator() {
        database.getReference("followList").removeValue()
        database.getReference("followers").removeValue()
    }

    private fun followRepository(currentUser: AuthRepositoryInterface = auth): FollowRepositoryInterface {
        return FollowRepository(currentUser, database, ioDispatcher)
    }

    @Test
    fun followUserUpdatesDatabase() = runTest {
        val follow = followRepository()

        follow.follow(mockUser(id = "follow1"))

        val follows = database.getReference("followList/$currentUserId/follow1").get().await().getValue(Boolean::class.java)
        assertNotNull(follows)
        assertTrue(follows!!)

        val followed = database.getReference("followers/follow1/$currentUserId").get().await().getValue(Boolean::class.java)
        assertNotNull(followed)
        assertTrue(followed!!)

        val nbFollowers = database.getReference("followers/follow1/:count").get().await().getValue(Long::class.java)
        assertNotNull(nbFollowers)
        assertEquals(1L, nbFollowers)
    }

    @Test
    fun unfollowRemovesEntries() = runTest {
        val follow = followRepository()
        val mockUser = mockUser(id = "follow1")
        follow.follow(mockUser)

        follow.unfollow(mockUser)

        val follows = database.getReference("followList/$currentUserId/follow1").get().await().getValue(Boolean::class.java)
        assertNull(follows)

        val followed = database.getReference("followers/follow1/$currentUserId").get().await().getValue(Boolean::class.java)
        assertNull(followed)

        val nbFollowers = database.getReference("followers/follow1/:count").get().await().getValue(Long::class.java)
        assertNotNull(nbFollowers)
        assertEquals(0L, nbFollowers)
    }

    @Test
    fun doesFollowCorrectlyUpdates() = runTest {
        val follow = followRepository()
        val mockUser = mockUser(id = "follow1")

        val doesFollow = follow.doesFollow(mockUser)
        val doesFollow2 = follow.doesFollow("follow1")
        val doesFollow3 = follow.doesFollow(currentUserId, "follow1")
        assertFalse(doesFollow.first())
        assertFalse(doesFollow2.first())
        assertFalse(doesFollow3.first())

        follow.follow(mockUser)
        assertTrue(doesFollow.first())
        assertTrue(doesFollow2.first())
        assertTrue(doesFollow3.first())
    }

    @Test
    fun getCurrentUserFollowCountUpdates() = runTest {
        val currentFollow = followRepository()
        val other = mockUser(id = "other")
        val otherFollow = followRepository(MockAuthRepository(other))

        val currentUserFollowCount = currentFollow.getCurrentUserFollowCount()
        assertEquals(0L, currentUserFollowCount.first())

        otherFollow.follow(auth.loggedUser!!)
        assertEquals(1L, currentUserFollowCount.first())

        val otherUserFollowCount = currentFollow.getFollowCount(other)
        assertEquals(0L, otherUserFollowCount.first())

        currentFollow.follow(other)
        assertEquals(1L, otherUserFollowCount.first())
    }

    @Test
    fun followListGetsUpdatedOnFollows() = runTest {
        val current = followRepository()
        val minions = (1..4).map { mockUser(id = "minion$it") }

        var currentNbFollows = 0
        val currentFollows = current.getFollowList()

        minions.forEach {
            val expected = minions.subList(0, currentNbFollows).map { minion -> minion.id }
            assertEquals(expected.toSet(), currentFollows.first().toSet())

            current.follow(it)
            currentNbFollows++
        }
    }
}