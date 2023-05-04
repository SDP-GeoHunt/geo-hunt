package com.github.geohunt.app.data.repository

import com.github.geohunt.app.data.exceptions.auth.UserNotLoggedInException
import com.github.geohunt.app.model.User
import kotlinx.coroutines.flow.Flow

interface FollowRepositoryInterface {
    /**
     * Returns the list of user IDs that the currently authenticated user follows.
     *
     * As each user ID is 20 ASCII characters long (= 20 bytes long), we can have up to
     * 50 000 IDs per megabyte. We expect that a normal user will not have more than ~2 000 IDs
     * in his follow list, which is ~40 Kb. Hence we do not need to paginate this query.
     */
    fun getFollowList(): Flow<List<String>>

    /**
     * Returns the number of followers of the given user.
     */
    fun getFollowCount(user: User): Flow<Long>

    /**
     * Returns the number of followers of the current user.
     */
    fun getCurrentUserFollowCount(): Flow<Long>

    /**
     * Make the currently authenticated user follow the given user.
     */
    @Throws(UserNotLoggedInException::class)
    suspend fun follow(user: User)

    /**
     * Make the currently authenticated user unfollow the given user.
     */
    @Throws(UserNotLoggedInException::class)
    suspend fun unfollow(user: User)

    /**
     * Returns the follow state of the currently authenticated user with regards to the given user.
     *
     * If there is no currently authenticated user, throws a [UserNotLoggedInException].
     */
    @Throws(UserNotLoggedInException::class)
    fun doesFollow(user: User): Flow<Boolean>

    /**
     * Returns the follow state of the currently authenticated user with regards to the given user.
     *
     * If there is no currently authenticated user, throws a [UserNotLoggedInException].
     */
    @Throws(UserNotLoggedInException::class)
    fun doesFollow(uid: String): Flow<Boolean>

    /**
     * Returns the follow state of whether userA's uid follows userB's uid
     *
     * @parma userAUid
     * @param userBUid
     */
    @Throws(UserNotLoggedInException::class)
    fun doesFollow(userAUid: String, userBUid: String): Flow<Boolean>
}