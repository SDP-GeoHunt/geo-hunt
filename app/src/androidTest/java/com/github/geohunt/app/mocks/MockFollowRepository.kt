package com.github.geohunt.app.mocks

import com.github.geohunt.app.data.repository.FollowRepositoryInterface
import com.github.geohunt.app.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

open class MockFollowRepository: FollowRepositoryInterface {
    override fun getFollowList(): Flow<List<String>> {
        return flowOf(listOf())
    }

    override fun getFollowCount(user: User): Flow<Long> {
        return flowOf(0)
    }

    override fun getCurrentUserFollowCount(): Flow<Long> {
        return flowOf(0)
    }

    override suspend fun follow(user: User) {
    }

    override suspend fun unfollow(user: User) {
    }

    override fun doesFollow(user: User): Flow<Boolean> {
        return flowOf(false)
    }

    override fun doesFollow(uid: String): Flow<Boolean> {
        return flowOf(false)
    }
}