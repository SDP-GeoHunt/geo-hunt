package com.github.geohunt.app.data.repository

import com.github.geohunt.app.data.exceptions.auth.UserNotLoggedInException
import com.github.geohunt.app.data.network.firebase.toList
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.snapshots
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

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