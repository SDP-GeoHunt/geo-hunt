package com.github.geohunt.app.data.repository.bounties

import com.github.geohunt.app.data.repository.UserRepositoryInterface
import com.github.geohunt.app.model.Team
import com.github.geohunt.app.utility.toMap
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.snapshots
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Creates a teams repository for the specific bounty.
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

    override suspend fun getTeam(teamId: String): Flow<Team> {
        return teams.child(teamId).snapshots
                .map { snapshotToTeam(it) }
                .flowOn(ioDispatcher)
    }

    override suspend fun getTeams(): Flow<List<Team>> {
        return teams.snapshots
                .map { it -> it.children.map { snapshotToTeam(it) } }
                .flowOn(ioDispatcher)
    }

    override suspend fun createTeam(teamLeaderUid: String): Team {
        return withContext(ioDispatcher) {
            val newTeamReference = teams.push()
            val teamId = newTeamReference.key!!

            // set the leader
            newTeamReference.child("teamLeader").setValue(teamLeaderUid)

            joinTeam(teamId, teamLeaderUid)

            Team(
                teamId = teamId,
                membersUid = listOf(teamLeaderUid),
                leaderUid = teamLeaderUid
            )
        }
    }


    private fun snapshotToTeam(s: DataSnapshot): Team {
        return Team(
            teamId = s.key!!,
            membersUid = s.child("members").toMap<Boolean>().filterValues { it }.keys.toList(),
            leaderUid = s.child("teamLeader").getValue(String::class.java) ?: ""
        )
    }
}