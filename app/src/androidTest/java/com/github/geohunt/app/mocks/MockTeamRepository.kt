package com.github.geohunt.app.mocks

import com.github.geohunt.app.data.repository.bounties.TeamsRepositoryInterface
import com.github.geohunt.app.model.Team
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class MockTeamRepository : TeamsRepositoryInterface {
    var team: MutableMap<String, MutableList<String>> = mutableMapOf()

    override suspend fun joinTeam(teamId: String, userId: String) {
        team.get(teamId)!!.add(userId)
    }

    override suspend fun joinTeam(teamId: String) {
        joinTeam(teamId, "1")
    }

    override fun getTeams(): Flow<List<Team>> {
        return flowOf(team.toList().map { Team(it.first, "<Team Name>", it.second, it.second.first(), 1000) })
    }

    override fun getTeam(teamId: String): Flow<Team> {
        return getTeams().map { it.find { it.teamId == teamId }!! }
    }

    override suspend fun createTeam(name: String, teamLeaderUid: String): Team {
        val newId = "-id-size-${team.size}"
        team.put(newId, mutableListOf(teamLeaderUid))
        return getTeam(newId).first()
    }

    override fun getUserTeam(userId: String): Flow<Team?> {
        TODO("Not implemented")
    }

    override suspend fun getUserTeam(): Flow<Team?> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserTeamAsync(): Team {
        TODO("Not yet implemented")
    }

    override fun getTeamScore(team: Team): Flow<Long> {
        return flowOf(team.score)
    }
}