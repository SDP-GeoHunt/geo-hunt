package com.github.geohunt.app.ui.screens.bounty_team_select

import com.github.geohunt.app.data.repository.ChallengeRepositoryInterface
import com.github.geohunt.app.data.repository.UserRepositoryInterface
import com.github.geohunt.app.data.repository.bounties.TeamsRepositoryInterface
import com.github.geohunt.app.mocks.MockBountiesRepositories
import com.github.geohunt.app.mocks.MockChallenge
import com.github.geohunt.app.mocks.MockChallengeRepository
import com.github.geohunt.app.mocks.MockTeamRepository
import com.github.geohunt.app.mocks.MockUserRepository
import com.github.geohunt.app.model.Bounty
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.model.Team
import com.github.geohunt.app.test_utils.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class BountyTeamSelectViewModelTest {
    private fun createFakeViewModel(
        challengeRepository: ChallengeRepositoryInterface = MockChallengeRepository(),
        userRepository: UserRepositoryInterface = MockUserRepository(),
        teamsRepository: TeamsRepositoryInterface = MockTeamRepository()
    ): BountyTeamSelectViewModel {
        return BountyTeamSelectViewModel(
            "1",
            bountiesRepository = object: MockBountiesRepositories() {
                override suspend fun getBountyById(bid: String): Bounty {
                    return Bounty("1", "hello", "1", LocalDateTime.MIN, LocalDateTime.MAX, Location(0.0, 0.0))
                }
            },
            challengeRepository = challengeRepository,
            userRepository = userRepository,
            teamsRepository = teamsRepository
        )
    }


    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun fetchesTheTeamCorrectly() = runTest {
        val fakeTeams = listOf(
            Team(leaderUid = "1", name="a", membersUid = listOf("1", "2", "3"), teamId = "1", score = 100),
            Team(leaderUid = "1", name="b", membersUid = listOf("1", "2", "3"), teamId = "2", score = 100),
            Team(leaderUid = "1", name="c", membersUid = listOf("1", "2", "3"), teamId = "3", score = 100)
        )
        val teamsRepository = object: MockTeamRepository() {
            override fun getTeams(): Flow<List<Team>> {
                return flowOf(fakeTeams)
            }
        }
        val vm = createFakeViewModel(
            teamsRepository = teamsRepository
        )
        val teamsFromVM = vm.teams.first { it != null }
        assert(teamsFromVM!! == fakeTeams)
    }

    @Test
    fun fetchesTeamMembersCorrectly() = runTest {
        val fakeTeams = listOf(
            Team(leaderUid = "1", membersUid = listOf("1", "2", "3"), teamId = "1", score = 100, name = "a"),
            Team(leaderUid = "1", membersUid = listOf("1", "2"), teamId = "2", score = 100, name = "b")
        )
        val teamsRepository = object: MockTeamRepository() {
            override fun getTeams(): Flow<List<Team>> {
                return flowOf(fakeTeams)
            }
        }
        val vm = createFakeViewModel(teamsRepository = teamsRepository)

        val expected = mapOf(
            "1" to listOf(MockUserRepository.user1, MockUserRepository.user2, MockUserRepository.user3),
            "2" to listOf(MockUserRepository.user1, MockUserRepository.user2)
        )

        val usersFromVM = vm.users.first { it.containsKey("1") && it.containsKey("2") }

        assert(usersFromVM == expected)
    }

    @Test
    fun fetchesChallengesCorrectly() = runTest {
        val fakeChallenges = (1..10).map { MockChallenge() }
        val challengeRepository = object: MockChallengeRepository() {
            override suspend fun getChallenges(): List<Challenge> {
                return fakeChallenges
            }
        }
        val vm = createFakeViewModel(challengeRepository = challengeRepository)
        val challengesFromVM = vm.challenges.first { it != null }
        assert(challengesFromVM == fakeChallenges)
    }

    @Test
    fun joinTeamTriggersJoinTeamOfRepository() = runTest {
        val cf = MutableStateFlow(false)
        val fakeTeamRepository = object: MockTeamRepository() {
            override suspend fun joinTeam(teamId: String) {
                assert(teamId == "1")
                cf.value = true
            }
        }
        val vm = createFakeViewModel(teamsRepository = fakeTeamRepository)
        vm.joinTeam("1")
        cf.first { it }
    }

    @Test
    fun createTeamTriggersCreateTeamOfRepository() = runTest {
        val cf = MutableStateFlow(false)
        val fakeTeamRepository = object: MockTeamRepository() {
            override suspend fun createTeam(name: String): Team {
                cf.value = true
                return Team("1", "a", listOf(), "1", 0)
            }
        }
        val vm = createFakeViewModel(teamsRepository = fakeTeamRepository)
        vm.createOwnTeam("a")
        cf.first { it }
    }

    @Test
    fun leaveTeamsTriggersLeaveTeamOfRepository() = runTest {
        val cf = MutableStateFlow(false)
        val fakeTeamRepository = object: MockTeamRepository() {
            override suspend fun leaveTeam() {
                cf.value = true
            }
        }
        val vm = createFakeViewModel(teamsRepository = fakeTeamRepository)
        vm.leaveCurrentTeam()
        cf.first { it }
    }
}