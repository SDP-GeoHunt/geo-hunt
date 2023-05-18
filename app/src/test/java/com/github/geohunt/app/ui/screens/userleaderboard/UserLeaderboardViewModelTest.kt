package com.github.geohunt.app.ui.screens.userleaderboard

import com.github.geohunt.app.mocks.MockScoreRepository
import com.github.geohunt.app.mocks.MockUserRepository
import com.github.geohunt.app.mocks.mockUser
import com.github.geohunt.app.model.User
import com.github.geohunt.app.test_utils.MainCoroutineRule
import com.github.geohunt.app.ui.components.leaderboard.LeaderboardEntry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class UserLeaderboardViewModelTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun modelCorrectlyCreatesEntries() = runTest{
        val score = MockScoreRepository()
        for (i in 1L..3L) {
            score.incrementUserScore(i.toString(), i)
        }
        val user = MockUserRepository()
        val viewModel = UserLeaderboardViewModel(scoreRepository = score, userRepository = user)

        advanceUntilIdle()

        val info = viewModel.leaderboardInformation.first()
        Assert.assertEquals(2, info.userIndex)

        val expectedEntries = listOf(
                LeaderboardEntry("dn3", 3, null),
                LeaderboardEntry("dn2", 2, null),
                LeaderboardEntry("dn", 1, null)
        )
        Assert.assertEquals(expectedEntries, info.entries)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun modelOnlyReturnsNEntries() = runTest{
        val n = UserLeaderboardViewModel.nbUsersFetched
        val users = (0L .. n + 1).map { mockUser("$it", "u$it") }

        //Create a mock repository containing n + 2 users
        val user = object: MockUserRepository() {
            override suspend fun getUser(id: String): User {
                return users[id.toInt()]
            }

            override suspend fun getCurrentUser(): User {
                return users[0]
            }
        }

        //Give each user a score
        val score = MockScoreRepository()
        users.forEachIndexed { index, it -> score.incrementUserScore(it, index.toLong()) }

        val viewModel = UserLeaderboardViewModel(score, user)

        advanceUntilIdle()

        val info = viewModel.leaderboardInformation.first()
        Assert.assertEquals(n, info.entries.size)
        //The user shouldn't appear on the leaderboard since he's not in the top 100
        Assert.assertEquals(-1, info.userIndex)
        Assert.assertEquals(-1, info.entries.indexOfFirst { it.displayName == users[0].displayName })
    }
}