package com.github.geohunt.app.data.repository.bounties

import kotlinx.coroutines.flow.Flow

/**
 * Stores all the bounties the user joined
 */
interface ActiveBountiesRepositoryInterface {
    /**
     * Adds given bounty to the list of bounties the currently logged user joined
     * Has to be called when a user joins a new bounty
     * @param bid the bounty id of the bounty we want to join
     */
    suspend fun joinBounty(bid: String)

    /**
     * Removes given bounty to the list of bounties the currently logged user joined
     * Has to be called when a user leaves a bounty
     * @param bid the bounty id of the bounty we want to leave
     */
    suspend fun leaveBounty(bid: String)

    /**
     * Returns the list of bounties the user joined in the form of a Flow
     */
    fun getBounties(): Flow<List<String>>
}