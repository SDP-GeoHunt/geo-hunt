package com.github.geohunt.app.mocks

import com.github.geohunt.app.data.repository.ActiveHuntsRepositoryInterface
import com.github.geohunt.app.model.Challenge
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

open class MockActiveHuntRepository : ActiveHuntsRepositoryInterface {
    override suspend fun joinHunt(challenge: Challenge) {
    }

    override suspend fun leaveHunt(challenge: Challenge) {
    }

    override fun getHunters(challenge: Challenge): Flow<List<String>> {
        return flowOf()
    }

    override fun getActiveHunts(): Flow<List<String>> {
        return flowOf(listOf())
    }

    override fun isHunting(challenge: Challenge): Flow<Boolean> {
        return flowOf(false)
    }
}