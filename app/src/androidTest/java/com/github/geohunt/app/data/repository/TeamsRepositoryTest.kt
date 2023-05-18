package com.github.geohunt.app.data.repository

import com.github.geohunt.app.data.repository.bounties.TeamsRepository
import com.github.geohunt.app.mocks.MockUserRepository
import com.github.geohunt.app.model.database.FirebaseEmulator
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TeamsRepositoryTest {
    private lateinit var database: FirebaseDatabase

    @Before
    fun setupEmulator() {
        database = FirebaseEmulator.getEmulatedFirebase()
    }

    @Test
    fun createsProperlyATeam() = runTest {
        val repo = TeamsRepository(
            bountyReference = database.getReference("bounty/test-bounty"),
            MockUserRepository()
        )
        val t = repo.createTeam("name", "1")
        assert(
            getFromDb(database, "bounty/test-bounty/teams/${t.teamId}/teamLeader", String::class.java)
            == "1"
        )
        assert(
            getFromDb(database, "bounty/test-bounty/teams/${t.teamId}/members/1", Boolean::class.java)
        )
    }


    @Test
    fun joiningATeamEmitsOnSpecificTeamFlow() = runTest {
        val repo = TeamsRepository(
            bountyReference = database.getReference("bounty/test-bounty"),
            MockUserRepository()
        )
        val createdTeam = repo.createTeam("name", "1")
        val flow = repo.getTeam(createdTeam.teamId)
        val firstInstance = flow.first()
        assert(!firstInstance.membersUid.contains("2"))
        repo.joinTeam(createdTeam.teamId, "2")
        val secondInstance = flow.first()
        assert(secondInstance.membersUid.contains("2"))
        // Clean
        repo.leaveTeam("2")
        repo.leaveTeam("1")
    }

    @Test
    fun joiningTeamAsLoggedUserJoinsProperly() = runTest {
        val repo = TeamsRepository(
            bountyReference = database.getReference("bounty/test-bounty"),
            MockUserRepository()
        )
        val createdTeam = repo.createTeam("name", "2")
        repo.joinTeam(createdTeam.teamId)
        val t = repo.getTeam(createdTeam.teamId).first()
        assert(t.membersUid.contains("1"))
        // Clean
        repo.leaveTeam("1")
        repo.leaveTeam("2")
    }

    @Test
    fun leavingTeamDoesLeaveTeam() = runTest {
        val repo = TeamsRepository(
            bountyReference = database.getReference("bounty/test-bounty"),
            MockUserRepository()
        )
        val createdTeam = repo.createTeam("name", "1")
        val members = repo.getTeam(createdTeam.teamId).first().membersUid
        repo.leaveTeam()
        val members2 = repo.getTeam(createdTeam.teamId).first().membersUid
        assert(members2.isEmpty() && members.size == 1)
    }

    @Test
    fun creatingATeamEmitsOnFlow() = runTest {
        val repo = TeamsRepository(
            bountyReference = database.getReference("bounty/test-bounty"),
            MockUserRepository()
        )
        val flow = repo.getTeams()
        val firstInstance = flow.first()
        val nbOfTeamsInFirstInstance = firstInstance.size
        repo.createTeam("name", "1")
        val secondInstance = flow.first()
        assert(secondInstance.size == nbOfTeamsInFirstInstance + 1)
        assert(secondInstance.subtract(firstInstance).size == 1)
        repo.leaveTeam()
    }

    @Test
    fun deletingATeamDeletesIt() = runTest {
        val repo = TeamsRepository(
            bountyReference = database.getReference("bounty/test-bounty"),
            MockUserRepository()
        )
        val flow = repo.getTeams()
        val firstInstance = flow.first()
        val team = repo.createTeam("caca", "1")
        val secondInstance = flow.first()
        repo.deleteTeam(team)
        val thirdInstance = flow.first()
        assert(
            firstInstance.size == thirdInstance.size && secondInstance.size == firstInstance.size + 1
        )
    }



    private suspend fun <T> getFromDb(r: FirebaseDatabase, p: String, c: Class<T>): T {
        return r.getReference(p).get().await().getValue(c)!!
    }
}