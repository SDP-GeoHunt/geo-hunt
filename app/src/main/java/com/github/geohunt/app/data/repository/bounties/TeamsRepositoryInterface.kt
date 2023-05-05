package com.github.geohunt.app.data.repository.bounties

import com.github.geohunt.app.model.Team
import com.github.geohunt.app.model.User
import kotlinx.coroutines.flow.Flow

interface TeamsRepositoryInterface {


    /**
     * Joins the user to the team
     *
     * @param teamId The team's id to join
     * @param userId The user to add in the team
     */
    suspend fun joinTeam(teamId: String, userId: String)

    /**
     * Joins the current authenticated user to the team
     */
    suspend fun joinTeam(teamId: String)

    /**
     * Returns the list of teams that are in the bounty
     */
    suspend fun getTeams(): Flow<List<Team>>

    /**
     * Returns the team by the teamId
     *
     * @param teamId The team's id
     */
    suspend fun getTeam(teamId: String): Flow<Team>

    /**
     * Creates a new team for the bounty with the given user
     * as the team leader.
     *
     * @param teamLeader The team leader
     */
    suspend fun createTeam(teamLeader: User): Team = createTeam(teamLeader.id)

    /**
     * Creates a new team for the bounty with the given user
     * as the team leader.
     *
     * @param teamLeaderUid The team leader's user id
     */
    suspend fun createTeam(teamLeaderUid: String): Team
}