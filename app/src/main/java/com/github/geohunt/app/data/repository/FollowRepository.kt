package com.github.geohunt.app.data.repository

import com.github.geohunt.app.data.exceptions.auth.UserNotLoggedInException
import com.github.geohunt.app.data.network.firebase.toList
import com.github.geohunt.app.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.snapshots
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Contains methods to follow and unfollow users, as well as retrieve the follow list and the list
 * of followers of users.
 */
class FollowRepository(
    private val authRepository: AuthRepository,
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    /**
     * Returns the list of user IDs that the currently authenticated user follows.
     *
     * As each user ID is 20 ASCII characters long (= 20 bytes long), we can have up to
     * 50 000 IDs per megabyte. We expect that a normal user will not have more than ~2 000 IDs
     * in his follow list, which is ~40 Kb. Hence we do not need to paginate this query.
     */
    fun getFollowList(): Flow<List<String>> {
        authRepository.requireLoggedIn()

        val currentUser = authRepository.getCurrentUser()

        return database.getReference("followList/${currentUser.id}")
            .snapshots
            .map(DataSnapshot::toList)
            .flowOn(ioDispatcher)
    }

    /**
     * Returns the number of followers of the given user.
     */
    fun getFollowCount(user: User): Flow<Long> =
        database.getReference("followers/${user.id}/:count")
            .snapshots
            .map { it.getValue(Long::class.java) ?: 0 }
            .flowOn(ioDispatcher)

    /**
     * Returns the number of followers of the current user.
     */
    fun getCurrentUserFollowCount(): Flow<Long> {
        authRepository.requireLoggedIn()
        return getFollowCount(authRepository.getCurrentUser())
    }

    /**
     * Updates the follow state of the currently authenticated user with regards to the given user.
     *
     * Custom security rules on the backend ensure that the user does not follow someone already
     * followed, or unfollows someone not followed, to make sure that the number of followers
     * stays coherent. However, this is only a safety net, and callers should ensure that it is not
     * the case when they call this method.
     *
     * The UI should proceed with caution, and never authorize a follow action before verifying
     * state. Use [doesFollow] to get the follow state before e.g. showing the follow button.
     */
    private suspend fun updateFollowState(user: User, follow: Boolean) {
        authRepository.requireLoggedIn()

        val currentUser = authRepository.getCurrentUser()

        // In spite of its appearance, this code does **not** update all locations at once, but
        // rather sequentially.
        val updates = mapOf(
            "followList/${currentUser.id}/${user.id}" to true.takeIf { follow },
            "followers/${user.id}/${currentUser.id}" to true.takeIf { follow },
            "followers/${user.id}/:count" to ServerValue.increment(if (follow) 1 else -1)
        )

        withContext(ioDispatcher) {
            database.reference.updateChildren(updates).await()
        }
    }

    /**
     * Make the currently authenticated user follow the given user.
     *
     * There is no verification that the current user does not already follow the user. See
     * [updateFollowState] for more information on this warning.
     *
     * If there is no currently authenticated user, throws a [UserNotLoggedInException].
     *
     * @see [updateFollowState]
     */
    @Throws(UserNotLoggedInException::class)
    suspend fun follow(user: User) = updateFollowState(user, follow = true)

    /**
     * Make the currently authenticated user unfollow the given user.
     *
     * There is no verification that the current user follows the user being unfollowed. See
     * [updateFollowState] for more information on this warning.
     *
     * If there is no currently authenticated user, throws a [UserNotLoggedInException].
     *
     * @see [updateFollowState]
     */
    @Throws(UserNotLoggedInException::class)
    suspend fun unfollow(user: User) = updateFollowState(user, follow = false)

    /**
     * Returns the follow state of the currently authenticated user with regards to the given user.
     *
     * If there is no currently authenticated user, throws a [UserNotLoggedInException].
     */
    @Throws(UserNotLoggedInException::class)
    fun doesFollow(user: User): Flow<Boolean> {
        authRepository.requireLoggedIn()

        val currentUser = authRepository.getCurrentUser()

        // There are two locations where we could fetch this information
        // We consider that the user follow list is more appropriate, since it might already be in-cache
        return database.getReference("followList/${currentUser.id}/${user.id}")
            .snapshots
            .map { it.getValue(Boolean::class.java) ?: false }
            .flowOn(ioDispatcher)
    }
}