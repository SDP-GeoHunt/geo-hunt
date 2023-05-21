package com.github.geohunt.app.data.repository

import com.github.geohunt.app.data.exceptions.auth.UserNotLoggedInException
import com.github.geohunt.app.data.network.firebase.toList
import com.github.geohunt.app.model.Challenge
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.snapshots
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Contains methods related to the retrieval and bookmarking of active hunts.
 */
class ActiveHuntsRepository(
    private val authRepository: AuthRepository,
    database: FirebaseDatabase = FirebaseDatabase.getInstance(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ActiveHuntsRepositoryInterface {
    private val activeHunts = database.getReference("activeHunts")
    private val hunters = database.getReference("hunters")

    /**
     * Updates the hunt state of the currently authenticated user on the given challenge.
     *
     * If there are no currently authenticated user, throws a [UserNotLoggedInException].
     */
    private suspend fun updateHuntState(challenge: Challenge, doHunt: Boolean) {
        authRepository.requireLoggedIn()

        val currentUser = authRepository.getCurrentUser()

        withContext(ioDispatcher) {
            val value = if (doHunt) true else null

            activeHunts
                .child(currentUser.id)
                .child(challenge.id)
                .setValue(value)
                .await()

            hunters
                .child(challenge.id)
                .child(currentUser.id)
                .setValue(value)
                .await()
        }
    }

    /**
     * Makes the currently authenticated user join the hunt on the given challenge.
     *
     * This adds the challenge to his active hunts page.
     */
    override suspend fun joinHunt(challenge: Challenge) = updateHuntState(challenge, doHunt = true)

    /**
     * Makes the currently authenticated user leave the hunt on the given challenge.
     *
     * This removes the challenge to his active hunts page.
     */
    override suspend fun leaveHunt(challenge: Challenge) = updateHuntState(challenge, doHunt = false)


    /**
     * Returns the active hunts IDs of the currently authenticated user.
     *
     * If there is no currently authenticated user, throws a [UserNotLoggedInException].
     */
    override fun getActiveHunts(): Flow<List<String>> {
        authRepository.requireLoggedIn()

        val currentUser = authRepository.getCurrentUser()

        // List<String> should not be stored as is within the database,
        // prefer Map<String, Boolean>
        return activeHunts
            .child(currentUser.id)
            .snapshots
            .map {
                it.toList()
            }
            .flowOn(ioDispatcher)
    }

    /**
     * Returns the list of hunters of the given challenge.
     */
    override fun getHunters(challenge: Challenge): Flow<List<String>> {
        return hunters
            .child(challenge.id)
            .snapshots
            .map {
                it.toList()
            }
            .flowOn(ioDispatcher)
    }

    /**
     * Check whether or not the currently authenticated user hunt a specific challenges
     */
    override fun isHunting(challenge: Challenge) : Flow<Boolean> {
        authRepository.requireLoggedIn()
        val currentUser = authRepository.getCurrentUser()

        return activeHunts
            .child(currentUser.id)
            .child(challenge.id)
            .snapshots
            .map { dataSnapshot -> dataSnapshot.getValue(Boolean::class.java) ?: false  }
            .flowOn(ioDispatcher)
    }
}