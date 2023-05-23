package com.github.geohunt.app.ui.screens.activehunts

import com.github.geohunt.app.mocks.MockActiveBountiesRepository
import com.github.geohunt.app.mocks.MockActiveHuntRepository
import com.github.geohunt.app.mocks.MockAuthRepository
import com.github.geohunt.app.mocks.MockBountiesRepositories
import com.github.geohunt.app.mocks.MockChallenge
import com.github.geohunt.app.mocks.MockChallengeRepository
import com.github.geohunt.app.mocks.MockUserRepository
import com.github.geohunt.app.mocks.mockUser
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.User
import com.github.geohunt.app.test_utils.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class ActiveHuntsViewModelTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun viewModelCorrectlyFetchesChallenges() = runTest {
        val mockChallenges = listOf(MockChallenge("1"), MockChallenge("2"), MockChallenge("3"))

        val challenge = object : MockChallengeRepository() {
            override suspend fun getChallenge(id: String): Challenge {
                return mockChallenges[id.toInt() - 1]
            }
        }
        val activeHunts = object : MockActiveHuntRepository() {
            override fun getActiveHunts(): Flow<List<String>> {
                return flowOf(listOf("1", "2", "3"))
            }
        }
        val viewModel = ActiveHuntsViewModel(
                MockAuthRepository(),
                MockUserRepository(),
                activeHunts,
                challenge,
                MockActiveBountiesRepository(),
                MockBountiesRepositories()
                )

        advanceUntilIdle()

        val hunts = viewModel.activeHunts.value
        Assert.assertEquals(mockChallenges, hunts)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun viewModelCorrectlyFetchesAuthors() = runTest {
        val user = object : MockUserRepository() {
            override suspend fun getUser(id: String): User {
                require(id == "1")
                return mockUser(displayName = "Test")
            }
        }

        val viewModel = ActiveHuntsViewModel(
                MockAuthRepository(),
                user,
                MockActiveHuntRepository(),
                MockChallengeRepository(),
                MockActiveBountiesRepository(),
                MockBountiesRepositories()
        )

        val mockChallenge = MockChallenge(authorId = "1")
        val values = viewModel.getAuthorName(mockChallenge).take(2).toList()
        Assert.assertEquals("Loading …", values[0])
        Assert.assertEquals("Test", values[1])
    }
}