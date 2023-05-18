package com.github.geohunt.app.mocks

import com.github.geohunt.app.data.repository.bounties.TeamsRepositoryInterface
import com.github.geohunt.app.model.Team
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockTeamsRepository : TeamsRepositoryInterface {

    // This is a static list of teams for our mock
    private val teams = listOf(
        Team("team1", "Team 1", listOf("user1", "user2"), "user1", 100),
        Team("team2", "Team 2", listOf("user3", "user4"), "user3", 200)
    )

    override suspend fun joinTeam(teamId: String, userId: String) {
        // In a mock, this could be a no-op
    }

    override suspend fun leaveTeam(userId: String) {
        // In a mock, this could be a no-op
    }

    override fun getTeam(teamId: String): Flow<Team> {
        // Return the first team that matches the provided ID
        return flowOf(teams.first { it.teamId == teamId })
    }

    override suspend fun getUserTeam(): Flow<Team?> {
        // Return the first team for simplicity
        return flowOf(teams.first())
    }

    override fun getTeamScore(team: Team): Flow<Long> {
        // Return the score of the provided team
        return flowOf(team.score)
    }

    override fun getUserTeam(userId: String): Flow<Team?> {
        // Return the first team that contains the user ID
        return flowOf(teams.firstOrNull { it.membersUid.contains(userId) })
    }

    override fun getTeams(): Flow<List<Team>> {
        // Return all teams
        return flowOf(teams)
    }

    override suspend fun createTeam(name: String): Team {
        // Return a new static team
        return Team("team3", "Team 3", listOf("user5"), "user5", 300)
    }

    override suspend fun createTeam(name: String, teamLeaderUid: String): Team {
        // Return a new static team with the provided leader
        return Team("team3", "Team 3", listOf(teamLeaderUid), teamLeaderUid, 300)
    }

    override suspend fun deleteTeam(teamId: String) {
        // In a mock, this could be a no-op
    }

    // Aliases
    override suspend fun joinTeam(teamId: String) {
        // In a mock, this could be a no-op
    }

    override suspend fun leaveTeam() {
        // In a mock, this could be a no-op
    }
}
