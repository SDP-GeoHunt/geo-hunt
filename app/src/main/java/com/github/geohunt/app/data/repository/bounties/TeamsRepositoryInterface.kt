package com.github.geohunt.app.data.repository.bounties

import com.github.geohunt.app.data.exceptions.TeamNotFoundException
import com.github.geohunt.app.data.exceptions.auth.UserNotLoggedInException
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
    fun getTeams(): Flow<List<Team>>

    /**
     * Returns the team by the teamId
     *
     * @param teamId The team's id
     */
    fun getTeam(teamId: String): Flow<Team>

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

    /**
     * Returns the team in which the user with the given userId is enrolled.
     *
     * null if the user is enrolled in no teams.
     */
    fun getUserTeam(userId: String): Flow<Team?>

    /**
     * Returns the team in which the authenticated user is enrolled.
     *
     * null if the user is enrolled in no teams.
     */
    suspend fun getUserTeam(): Flow<Team?>

    /**
     * Get the team of the current user
     */
    @Throws(UserNotLoggedInException::class, TeamNotFoundException::class)
    suspend fun getUserTeamAsync() : Team

    /**
     * Returns the score of the specified team
     */
    fun getTeamScore(team: Team) : Flow<Long>
}