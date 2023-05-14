package com.github.geohunt.app.data.repository.bounties

import com.github.geohunt.app.data.exceptions.TeamNotFoundException
import com.github.geohunt.app.data.repository.AuthRepository
import com.github.geohunt.app.data.repository.AuthRepositoryInterface
import com.github.geohunt.app.data.repository.UserRepositoryInterface
import com.github.geohunt.app.model.Team
import com.github.geohunt.app.utility.toMap
import com.google.firebase.database.*
import com.google.firebase.database.ktx.snapshots
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Team repository for a specific bounty.
 */
class TeamsRepository(
    bountyReference: DatabaseReference,
    private val userRepository: UserRepositoryInterface,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): TeamsRepositoryInterface {
    private val teams = bountyReference.child("teams")

    override suspend fun joinTeam(teamId: String, userId: String) {
        withContext(ioDispatcher) {
            return@withContext teams.child(teamId).child("members")
                    .child(userId)
                    .setValue(true)
                    .await()
        }
    }

    override suspend fun joinTeam(teamId: String) {
        return joinTeam(teamId, userRepository.getCurrentUser().id)
    }

    override fun getTeam(teamId: String): Flow<Team> {
        return teams.child(teamId).snapshots
                .map { snapshotToTeam(it) }
                .flowOn(ioDispatcher)
    }

    override suspend fun getUserTeam(): Flow<Team?> = getUserTeam(userRepository.getCurrentUser().id)

    override suspend fun getUserTeamAsync(): Team = withContext(ioDispatcher) {
        val uid = userRepository.getCurrentUser().id

        teams.get().await().children
            .map { snapshotToTeam(it) }
            .first { it.membersUid.contains(uid) }
    }

    override fun getTeamScore(team: Team): Flow<Long> =
        teams.child(team.teamId).child("score").snapshots
            .map { it.getValue(Long::class.java) ?: throw TeamNotFoundException(team.teamId) }
            .flowOn(ioDispatcher)

    override fun getUserTeam(userId: String): Flow<Team?> {
        return getTeams().map {
            it.firstOrNull { team -> team.membersUid.contains(userId) }
        }
    }

    override fun getTeams(): Flow<List<Team>> {
        return teams.snapshots
                .map { it -> it.children.map { snapshotToTeam(it) } }
                .flowOn(ioDispatcher)
    }

    override suspend fun createTeam(name: String, teamLeaderUid: String): Team {
        return withContext(ioDispatcher) {
            val newTeamReference = teams.push()
            val teamId = newTeamReference.key!!

            // set the leader
            newTeamReference.child("name").setValue(name)
            newTeamReference.child("score").setValue(0)
            newTeamReference.child("teamLeader").setValue(teamLeaderUid)

            joinTeam(teamId, teamLeaderUid)

            Team(
                teamId = teamId,
                name = name,
                membersUid = listOf(teamLeaderUid),
                leaderUid = teamLeaderUid,
                score = 0
            )
        }
    }

    /**
     * Atomically update of the score
     */
    internal suspend fun atomicScoreAddAndAssign(team: Team, increment: Long) = withContext(ioDispatcher) {
        teams.child(team.teamId)
            .updateChildren(
                hashMapOf("score" to ServerValue.increment(increment))
            )
            .await()
    }

    private fun snapshotToTeam(s: DataSnapshot): Team {
        return Team(
            teamId = s.key!!,
            name = s.child("name").getValue(String::class.java)!!,
            membersUid = s.child("members").toMap<Boolean>().filterValues { it }.keys.toList(),
            leaderUid = s.child("teamLeader").getValue(String::class.java) ?: "",
            score = s.child("score").getValue(Long::class.java) ?: 0
        )
    }
}