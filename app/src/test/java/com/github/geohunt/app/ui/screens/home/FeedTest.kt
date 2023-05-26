package com.github.geohunt.app.ui.screens.home

import com.github.geohunt.app.data.repository.ActiveHuntsRepository
import com.github.geohunt.app.data.repository.ChallengeRepository
import com.github.geohunt.app.domain.GetChallengeHuntStateUseCase
import com.github.geohunt.app.mocks.MockChallenge
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.ui.components.buttons.ChallengeHuntState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class FeedTest {
    private val mockActiveHuntsRepository: ActiveHuntsRepository = mock()
    private val mockChallengeRepo: ChallengeRepository = mock()
    private val mockChallengeHuntStateUseCase: GetChallengeHuntStateUseCase = mock {
        on { getChallengeHuntState(any()) } doReturn flowOf(ChallengeHuntState.NOT_HUNTED)
    }

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var feed: Feed

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun challengesHasAllChallengesOfFlow() = runTest {
        val challenges: List<Challenge> = listOf(MockChallenge(id = "1"), MockChallenge(id = "2"), MockChallenge(id = "3"))

        feed = Feed(
            challengeFlow = flowOf(challenges),
            coroutineScope = this,
            activeHuntsRepository = mockActiveHuntsRepository,
            challengeRepository = mockChallengeRepo,
            getChallengeHuntStateUseCase = mockChallengeHuntStateUseCase
        )

        advanceUntilIdle()
        assertThat(feed.challenges.value, equalTo(challenges))
    }

    @Test
    fun getAuthorCallsChallengeRepository() = runTest {
        val challenges: List<Challenge> = listOf(MockChallenge(authorId = "1"))

        feed = Feed(
            challengeFlow = flowOf(challenges),
            coroutineScope = this,
            activeHuntsRepository = mockActiveHuntsRepository,
            challengeRepository = mockChallengeRepo,
            getChallengeHuntStateUseCase = mockChallengeHuntStateUseCase
        )

        advanceUntilIdle()
        feed.getAuthor(challenges[0]).first()
        advanceUntilIdle()

        verify(mockChallengeRepo).getAuthor(challenges[0])
    }

    @Test
    fun getChallengeHuntStateCallsUseCase() = runTest {
        val challenges: List<Challenge> = listOf(MockChallenge(authorId = "1"))

        feed = Feed(
            challengeFlow = flowOf(challenges),
            coroutineScope = this,
            activeHuntsRepository = mockActiveHuntsRepository,
            challengeRepository = mockChallengeRepo,
            getChallengeHuntStateUseCase = mockChallengeHuntStateUseCase
        )

        advanceUntilIdle()
        feed.getChallengeHuntState(challenges[0]).first()
        advanceUntilIdle()

        verify(mockChallengeHuntStateUseCase).getChallengeHuntState(challenges[0])
    }

    @Test
    fun huntCallsActiveHuntsRepository() = runTest {
        val challenges: List<Challenge> = listOf(MockChallenge(authorId = "1"))

        feed = Feed(
            challengeFlow = flowOf(challenges),
            coroutineScope = this,
            activeHuntsRepository = mockActiveHuntsRepository,
            challengeRepository = mockChallengeRepo,
            getChallengeHuntStateUseCase = mockChallengeHuntStateUseCase
        )

        advanceUntilIdle()
        feed.getAuthor(challenges[0]) // first put the challenge in the cache
        advanceUntilIdle()
        feed.hunt(challenges[0])
        advanceUntilIdle()

        verify(mockActiveHuntsRepository).joinHunt(challenges[0])
    }

    @Test
    fun leaveHuntCallsActiveHuntsRepository() = runTest {
        val challenges: List<Challenge> = listOf(MockChallenge(authorId = "1"))

        feed = Feed(
            challengeFlow = flowOf(challenges),
            coroutineScope = this,
            activeHuntsRepository = mockActiveHuntsRepository,
            challengeRepository = mockChallengeRepo,
            getChallengeHuntStateUseCase = mockChallengeHuntStateUseCase
        )

        advanceUntilIdle()
        feed.getAuthor(challenges[0]) // first put the challenge in the cache
        advanceUntilIdle()
        feed.leaveHunt(challenges[0])
        advanceUntilIdle()

        verify(mockActiveHuntsRepository).leaveHunt(challenges[0])
    }
}