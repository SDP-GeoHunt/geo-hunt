package com.github.geohunt.app.mocks

import com.github.geohunt.app.data.repository.ActiveHuntsRepositoryInterface
import com.github.geohunt.app.model.Challenge
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

open class MockActiveHuntRepository(val activeHunts : List<String> = listOf()) : ActiveHuntsRepositoryInterface {
    override suspend fun joinHunt(challenge: Challenge) {
    }

    override suspend fun leaveHunt(challenge: Challenge) {
    }

    override fun getActiveHunts(): Flow<List<String>> {
        return flowOf(activeHunts)
    }

    override fun isHunting(challenge: Challenge): Flow<Boolean> {
        return flowOf(activeHunts.contains(challenge.id))
    }
}