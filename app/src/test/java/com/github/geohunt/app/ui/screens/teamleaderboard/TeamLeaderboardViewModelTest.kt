package com.github.geohunt.app.ui.screens.teamleaderboard

import com.github.geohunt.app.mocks.MockTeamRepository
import com.github.geohunt.app.model.Team
import com.github.geohunt.app.test_utils.MainCoroutineRule
import com.github.geohunt.app.ui.components.leaderboard.LeaderboardEntry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class TeamLeaderboardViewModelTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun modelCorrectlyCreatesEntries() = runTest {
        val fakeTeams = listOf(
                Team(leaderUid = "1", name="a", membersUid = listOf("1", "2", "3"), teamId = "1", score = 10),
                Team(leaderUid = "1", name="b", membersUid = listOf("1", "2", "3"), teamId = "2", score = 100),
                Team(leaderUid = "1", name="c", membersUid = listOf("1", "2", "3"), teamId = "3", score = 50)
        )
        val teams = object: MockTeamRepository() {
            override fun getTeams(): Flow<List<Team>> {
                return flowOf(fakeTeams)
            }

            override suspend fun getUserTeamAsync(): Team {
                return fakeTeams[0]
            }
        }

        val model = TeamLeaderboardViewModel(teams)

        advanceUntilIdle()

        val info = model.leaderboardInformation.first()
        val expectedEntries = listOf(fakeTeams[1], fakeTeams[2], fakeTeams[0]).map {
            LeaderboardEntry(it.name, it.score, null)
        }
        Assert.assertEquals(expectedEntries, info.entries)
        Assert.assertEquals(2, info.userIndex)
    }
}