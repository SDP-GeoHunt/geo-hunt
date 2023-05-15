package com.github.geohunt.app.data.repository

import com.github.geohunt.app.model.User

interface ScoreRepositoryInterface {
    suspend fun getScore(user: User): Long {
        return getScore(user.id)
    }

    suspend fun getScore(uid: String): Long

    suspend fun getTopNUsers(n: Int): List<Pair<String, Long>>

    suspend fun incrementUserScore(user: User, increment: Long)
}