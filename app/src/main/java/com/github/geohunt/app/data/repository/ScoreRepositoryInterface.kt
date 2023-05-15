package com.github.geohunt.app.data.repository

import com.github.geohunt.app.model.User

interface ScoreRepositoryInterface {
    suspend fun getScore(user: User): Long

    suspend fun getTopNUsers(n: Int): List<String>

    suspend fun incrementUserScore(user: User, increment: Long)
}