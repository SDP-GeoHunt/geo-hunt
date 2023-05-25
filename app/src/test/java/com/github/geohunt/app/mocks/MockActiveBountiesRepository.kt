package com.github.geohunt.app.mocks

import com.github.geohunt.app.data.repository.bounties.ActiveBountiesRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockActiveBountiesRepository: ActiveBountiesRepositoryInterface {
    override suspend fun joinBounty(bid: String) {
    }

    override suspend fun leaveBounty(bid: String) {
    }

    override fun getBounties(): Flow<List<String>> {
        return flowOf(listOf())
    }
}