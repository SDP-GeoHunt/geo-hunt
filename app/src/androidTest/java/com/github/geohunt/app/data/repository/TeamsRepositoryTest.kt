package com.github.geohunt.app.data.repository

import com.github.geohunt.app.data.repository.bounties.TeamsRepository
import com.github.geohunt.app.mocks.MockUserRepository
import com.github.geohunt.app.model.database.FirebaseEmulator
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
        val t = repo.createTeam("1")
        assert(
            getFromDb(database, "bounty/test-bounty/teams/${t.teamId}/teamLeader", String::class.java)
            == "1"
        )
        assert(
            getFromDb(database, "bounty/test-bounty/teams/${t.teamId}/members/1", Boolean::class.java)
        )
    }

    /*
    @Test
    fun joiningATeamEmitsOnFlow() = runTest {
        val repo = TeamsRepository(
            bountyReference = database.getReference("bounty/test-bounty"),
            MockUserRepository()
        )
        val createdTeam = repo.createTeam("1")
        val flow = repo.getTeam("1")
        flow.collect {
            it.teamId
        }
        Thread.sleep(10000)
        assert(!flow.single().membersUid.contains("2"))
        repo.joinTeam(createdTeam.teamId, "2")
        assert(flow.single().membersUid.contains("2"))
    }

     */


    private suspend fun <T> getFromDb(r: FirebaseDatabase, p: String, c: Class<T>): T {
        return r.getReference(p).get().await().getValue(c)!!
    }
}