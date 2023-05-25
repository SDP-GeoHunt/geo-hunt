package com.github.geohunt.app.data.repository

import com.github.geohunt.app.data.exceptions.ClaimNotFoundException
import com.github.geohunt.app.data.exceptions.auth.UserNotLoggedInException
import com.github.geohunt.app.data.local.LocalPicture
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Claim
import com.github.geohunt.app.model.User
import com.github.geohunt.app.model.Location
import kotlinx.coroutines.flow.Flow

interface ClaimRepositoryInterface {
    /**
     * Retrieve a list of all claims id for a specific user, useful when lazy loading
     */
    suspend fun getClaimId(user: User): List<String> {
        return getClaimId(user.id)
    }

    suspend fun getClaimId(uid: String): List<String>

    /**
     * Check whether the currently logged user claim the given challenges
     */
    suspend fun doesClaim(challenge: Challenge) : Boolean

    /**
     * Retrieve the score for a given user
     */
    suspend fun getScore(user: User) : Long

    /**
     * Get all claims of a specific user [user]. If one of his claim is not within the database
     * due to some internal issues then throws [ClaimNotFoundException]. Notice that this function
     * does not check whether the provided user exists or not !!
     */
    suspend fun getClaims(user: User): List<Claim> {
        return getClaims(user.id)
    }

    suspend fun getClaims(uid: String): List<Claim>

    /**
     * Retrieve a list of all claims associated with the current challenges
     */
    suspend fun getChallengeClaims(challenge: Challenge): List<Claim>

    /**
     * Claim a specific challenge with the given photo and location
     *
     * The claim will be claimed on the behalf of the currently logged used. If there
     * is none, throws a [UserNotLoggedInException]
     */
    @Throws(UserNotLoggedInException::class)
    suspend fun claimChallenge(
        photo: LocalPicture,
        location: Location,
        challenge: Challenge
    ): Claim
}