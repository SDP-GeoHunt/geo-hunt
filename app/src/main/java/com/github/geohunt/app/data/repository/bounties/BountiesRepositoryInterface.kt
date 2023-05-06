package com.github.geohunt.app.data.repository.bounties

import com.github.geohunt.app.data.exceptions.auth.UserNotLoggedInException
import com.github.geohunt.app.model.Bounty
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.model.User
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface BountiesRepositoryInterface {
    /**
     * Create a new bounty with a startingDate, an expirationDate and a location
     *
     * The bounty will be created on the behalf of the currently logged user,
     * if none throws a [UserNotLoggedInException]
     */
    @Throws(UserNotLoggedInException::class)
    suspend fun createBounty(
        startingDate: LocalDateTime,
        expirationDate: LocalDateTime,
        location: Location
    ) : Bounty

    /**
     * Retrieve the team repository corresponding with the current
     * bounty
     */
    fun getTeamRepository(bounty: Bounty) : TeamsRepositoryInterface

    /**
     * Get the bounty created by a given user
     */
    suspend fun getBountyCreatedBy(user: User) : List<Bounty>

    suspend fun getBounties() : List<Bounty>

    suspend fun getBountyById(bid: String) : Bounty
}