package com.github.geohunt.app.data.repository

import com.github.geohunt.app.model.User
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ScoreRepository(
        database: FirebaseDatabase = FirebaseDatabase.getInstance(),
        private val authRepository: AuthRepository,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ScoreRepositoryInterface {
    private val scoreRef = database.getReference("scores")

    override suspend fun getScore(user: User): Long = withContext(ioDispatcher) {
        scoreRef.child(user.id).get().await().getValue(Long::class.java) ?: 0
    }

    override suspend fun getTopNUsers(n: Int): List<String> {
        TODO("Not yet implemented")
    }

    override suspend fun incrementUserScore(user: User, increment: Long): Unit = withContext(ioDispatcher) {
        scoreRef.updateChildren(hashMapOf(
                user.id to ServerValue.increment(increment)
        )).await()
    }
}