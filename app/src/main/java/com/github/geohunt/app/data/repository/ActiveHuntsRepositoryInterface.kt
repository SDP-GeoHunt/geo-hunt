package com.github.geohunt.app.data.repository

import com.github.geohunt.app.data.exceptions.auth.UserNotLoggedInException
import com.github.geohunt.app.model.Challenge
import kotlinx.coroutines.flow.Flow

interface ActiveHuntsRepositoryInterface {

    /**
     * Makes the currently authenticated user join the hunt on the given challenge.
     *
     * This adds the challenge to his active hunts page.
     */
    suspend fun joinHunt(challenge: Challenge)

    /**
     * Makes the currently authenticated user leave the hunt on the given challenge.
     *
     * This removes the challenge to his active hunts page.
     */
    suspend fun leaveHunt(challenge: Challenge)

    /**
     * Returns the active hunts IDs of the currently authenticated user.
     *
     * If there is no currently authenticated user, throws a [UserNotLoggedInException].
     */
    fun getActiveHunts(): Flow<List<String>>

    /**
     * Returns the list of active hunters of the given challenge.
     */
    fun getHunters(challenge: Challenge): Flow<List<String>>

    /**
     * Check whether or not the currently authenticated user hunt a specific challenges
     */
    fun isHunting(challenge: Challenge) : Flow<Boolean>
}