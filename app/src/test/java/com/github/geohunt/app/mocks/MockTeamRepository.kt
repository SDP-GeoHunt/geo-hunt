package com.github.geohunt.app.mocks

import com.github.geohunt.app.data.repository.bounties.TeamsRepositoryInterface
import com.github.geohunt.app.model.Team
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

open class MockTeamRepository(
        private val teams: List<Team> = listOf()
): TeamsRepositoryInterface {
    override suspend fun joinTeam(teamId: String, userId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun joinTeam(teamId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun leaveTeam() {
        TODO("Not yet implemented")
    }

    override suspend fun leaveTeam(userId: String) {
        TODO("Not yet implemented")
    }

    override fun getTeams(): Flow<List<Team>> {
        return flowOf(teams)
    }

    override fun getTeam(teamId: String): Flow<Team> {
        return flowOf(teams.first { it.teamId == teamId } )
    }

    override suspend fun createTeam(name: String, teamLeaderUid: String): Team {
        TODO("Not yet implemented")
    }

    override suspend fun createTeam(name: String): Team {
        TODO("Not yet implemented")
    }


    override fun getUserTeam(userId: String): Flow<Team?> {
        return flowOf(teams.firstOrNull { it.membersUid.contains(userId) })
    }

    override suspend fun getUserTeam(): Flow<Team?> {
        return getUserTeam("1")
    }

    override fun getTeamScore(team: Team): Flow<Long> {
        return getTeam(team.teamId).map { it.score }
    }

    override suspend fun deleteTeam(teamId: String) {
        TODO("Not yet implemented")
    }
}