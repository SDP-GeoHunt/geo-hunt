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
     * Fails silently if the user is already in the team.
     *
     * @param teamId The team's id to join
     * @param userId The user to add in the team
     */
    suspend fun joinTeam(teamId: String, userId: String)

    /**
     * Joins the current authenticated user to the team
     *
     * Fails silently if the user is already in the team.
     */
    suspend fun joinTeam(teamId: String)

    /**
     * Make the authenticated user leave its current team
     *
     * Fails silently if the user was not in a team.
     */
    suspend fun leaveTeam()

    /**
     * Make the user with the given user id leave its current team
     *
     * Fails silently if the user was not in a team.
     */
    suspend fun leaveTeam(userId: String)

    /**
     * Make the given leave its current team
     *
     * Fails silently if the user was not in a team.
     */
    suspend fun leaveTeam(user: User) = leaveTeam(user.id)

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
     * @param name the name of the team
     * @param teamLeader The team leader
     */
    suspend fun createTeam(name: String, teamLeader: User): Team = createTeam(name, teamLeader.id)

    /**
     * Creates a new team for the bounty with the given user
     * as the team leader.
     *
     * @param name the name of the team
     * @param teamLeaderUid The team leader's user id
     */
    suspend fun createTeam(name: String, teamLeaderUid: String): Team

    /**
     * Creates a new team with the authenticated user as the leader
     *
     * @param name the name of the team
     */
    suspend fun createTeam(name: String): Team

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

    /**
     * Deletes the given team in the database. Fails silently if already deleted.
     *
     * @param team The team to delete
     */
    suspend fun deleteTeam(team: Team) = deleteTeam(team.teamId)

    /**
     * Deletes the team with the given team id. Fails silently if already deleted.
     *
     * @param teamId The team's id to delete
     */
    suspend fun deleteTeam(teamId: String)
}