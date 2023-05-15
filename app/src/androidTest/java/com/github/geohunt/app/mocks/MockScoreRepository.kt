package com.github.geohunt.app.mocks

import com.github.geohunt.app.data.repository.ScoreRepositoryInterface
import com.github.geohunt.app.model.User

open class MockScoreRepository: ScoreRepositoryInterface {
    override suspend fun getScore(uid: String): Long {
        return 0L
    }

    override suspend fun getTopNUsers(n: Int): List<Pair<String, Long>> {
        return listOf()
    }

    override suspend fun incrementUserScore(user: User, increment: Long) {
        TODO("Not yet implemented")
    }
}