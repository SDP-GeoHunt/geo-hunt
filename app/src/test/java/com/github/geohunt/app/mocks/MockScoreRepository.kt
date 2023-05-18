package com.github.geohunt.app.mocks

import com.github.geohunt.app.data.repository.ScoreRepositoryInterface

open class MockScoreRepository: ScoreRepositoryInterface {
    private val scores = hashMapOf<String, Long>().withDefault { 0L }
    override suspend fun getScore(uid: String): Long {
        return scores.getValue(uid)
    }

    override suspend fun getTopNUsers(n: Int): List<Pair<String, Long>> {
        return scores.entries
                .map { Pair(it.key, it.value) }
                .sortedBy { it.second }
                .reversed()
                .take(n)
    }

    override suspend fun incrementUserScore(uid: String, increment: Long) {
        scores[uid] = scores.getValue(uid) + increment
    }
}