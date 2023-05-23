package com.github.geohunt.app.data.repository.bounties

import kotlinx.coroutines.flow.Flow

/**
 * Stores all the bounties the user joined
 */
interface ActiveBountiesRepositoryInterface {
    suspend fun joinBounty(bid: String)

    suspend fun leaveBounty(bid: String)

    fun getBounties(): Flow<List<String>>
}