package com.github.geohunt.app.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * The implementation of the score repository interface
 * Handles the current scores of all the users
 */
class ScoreRepository(
        database: FirebaseDatabase = FirebaseDatabase.getInstance(),
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ScoreRepositoryInterface {
    private val scoreRef = database.getReference("scores")


    override suspend fun getScore(uid: String): Long = withContext(ioDispatcher) {
        scoreRef.child(uid).get().await().getValue(Long::class.java) ?: 0
    }

    override suspend fun getTopNUsers(n: Int): List<Pair<String, Long>> = withContext(ioDispatcher) {
        scoreRef
                .orderByValue()
                .limitToLast(n)
                .get().await().run {
                    children.map { it.key!! to (it.getValue(Long::class.java) ?: 0) }
                }.reversed()
    }

    override suspend fun incrementUserScore(uid: String, increment: Long): Unit = withContext(ioDispatcher) {
        scoreRef.updateChildren(
                hashMapOf( uid to ServerValue.increment(increment) )
        ).await()
    }
}