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
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val activeHunts = database.getReference("activeHunts")

    /**
     * Updates the hunt state of the currently authenticated user on the given challenge.
     *
     * If there are no currently authenticated user, throws a [UserNotLoggedInException].
     */
    private suspend fun updateHuntState(challenge: Challenge, doHunt: Boolean) {
        authRepository.requireLoggedIn()

        val currentUser = authRepository.getCurrentUser()

        withContext(ioDispatcher) {
            activeHunts
                .child(currentUser.id)
                .child(challenge.id)
                .setValue(true.takeIf { doHunt })
                .await()
        }
    }

    /**
     * Makes the currently authenticated user join the hunt on the given challenge.
     *
     * This adds the challenge to his active hunts page.
     */
    suspend fun joinHunt(challenge: Challenge) = updateHuntState(challenge, doHunt = true)

    /**
     * Makes the currently authenticated user leave the hunt on the given challenge.
     *
     * This removes the challenge to his active hunts page.
     */
    suspend fun leaveHunt(challenge: Challenge) = updateHuntState(challenge, doHunt = false)


    /**
     * Returns the active hunts IDs of the currently authenticated user.
     *
     * If there is no currently authenticated user, throws a [UserNotLoggedInException].
     */
    fun getActiveHunts(): Flow<List<String>> {
        authRepository.requireLoggedIn()

        val currentUser = authRepository.getCurrentUser()

        return activeHunts
            .child(currentUser.id)
            .snapshots
            .map { it.toList() }
            .flowOn(ioDispatcher)
    }
}