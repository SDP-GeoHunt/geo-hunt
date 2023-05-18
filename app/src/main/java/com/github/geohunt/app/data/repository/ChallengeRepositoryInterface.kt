package com.github.geohunt.app.data.repository

import com.github.geohunt.app.data.exceptions.ChallengeNotFoundException
import com.github.geohunt.app.data.exceptions.auth.UserNotLoggedInException
import com.github.geohunt.app.data.local.LocalPicture
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Claim
import com.github.geohunt.app.model.User
import com.github.geohunt.app.model.Location
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface ChallengeRepositoryInterface {

    /**
     * Fetches challenge with the given id on Firebase's Realtime Database.
     *
     * If there are no such challenge, throws a [ChallengeNotFoundException].
     *
     * @param id The unique id of the challenge, composed of a combination of the coarse hash and
     *           a randomly generated id.
     * @return The queried challenge.
     */

    @Throws(ChallengeNotFoundException::class)
    suspend fun getChallenge(id: String): Challenge

    /**
     * Returns all challenges in the given sector.
     *
     * @param sector The sector hash, as returned by [Location.getCoarseHash]
     */
    fun getSectorChallenges(sector: String): Flow<List<Challenge>>

    /**
     * Returns the author of the challenge.
     */
    suspend fun getAuthor(challenge: Challenge): User

    /**
     * Returns the URL of the challenge photo stored on Firebase Storage.
     */
    fun getChallengePhoto(challenge: Challenge): String

    /**
     * Returns the lists of posted challenges of the given user.
     */
    fun getPosts(user: User) = getPosts(user.id)

    /**
     * Returns the lists of posted challenges of the user with the given ID.
     */
    fun getPosts(userId: String): Flow<List<Challenge>>

    suspend fun getClaims(challenge: Challenge): List<Claim>

    /**
     * Creates a challenge with the given photo, location and options.
     *
     * The challenge will be created on behalf of the currently logged in user. If there are none,
     * throws a [UserNotLoggedInException].
     */
    @Throws(UserNotLoggedInException::class)
    suspend fun createChallenge(
        photo: LocalPicture,
        location: Location,
        difficulty: Challenge.Difficulty = Challenge.Difficulty.MEDIUM,
        expirationDate: LocalDateTime? = null,
        description: String? = null
    ): Challenge

    /**
     * Returns all the claims done by a specific user
     *
     * @param user The user
     */
    fun getClaimsFromUser(user: User) = getClaimsFromUser(user.id)


    /**
     * Returns all the claims done by a specific user id
     *
     * @param uid The user id
     */
    fun getClaimsFromUser(uid: String): List<Claim>

    /**
     * Get all the challenges for the challenge repository.
     *
     * This should be used only in the context of bounties to avoid
     * fetching a lot of challenges.
     */
    suspend fun getChallenges(): List<Challenge>
}