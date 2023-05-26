package com.github.geohunt.app.ui.screens.home

import com.github.geohunt.app.data.repository.ActiveHuntsRepository
import com.github.geohunt.app.data.repository.AuthRepository
import com.github.geohunt.app.data.repository.ChallengeRepository
import com.github.geohunt.app.data.repository.FollowRepository
import com.github.geohunt.app.data.repository.LocationRepository
import com.github.geohunt.app.domain.GetChallengeHuntStateUseCase
import com.github.geohunt.app.domain.GetUserFeedUseCase
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class HomeFeedViewModelTest {
    private val mockAuth: AuthRepository = mock()
    private val mockUserFeedUseCase: GetUserFeedUseCase = mock()
    private val mockChallengeHuntStateUseCase: GetChallengeHuntStateUseCase = mock()
    private val mockChallengeRepo: ChallengeRepository = mock()
    private val mockLocationRepo: LocationRepository = mock {
        on { getLocations(any()) } doReturn flowOf(Location(0.0, 0.0))
    }
    private val mockFollowRepository: FollowRepository = mock()
    private val mockActiveHuntsRepository: ActiveHuntsRepository = mock()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: HomeFeedViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        viewModel = HomeFeedViewModel(
            mockAuth,
            mockUserFeedUseCase,
            mockChallengeHuntStateUseCase,
            mockChallengeRepo,
            mockLocationRepo,
            mockFollowRepository,
            mockActiveHuntsRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun getUserLocationCallsLocationRepository() = runTest {
        advanceUntilIdle()

        verify(mockLocationRepo, times(1)).getLocations(argThat { true })
    }

    @Test
    fun followCallsFollowRepository() = runTest {
        advanceUntilIdle()

        val mockUser: User = mock()
        viewModel.follow(mockUser)
        advanceUntilIdle()

        verify(mockFollowRepository).follow(mockUser)
    }

    @Test
    fun unfollowCallsFollowRepository() = runTest {
        advanceUntilIdle()

        val mockUser: User = mock()
        viewModel.unfollow(mockUser)
        advanceUntilIdle()

        verify(mockFollowRepository).unfollow(mockUser)
    }

    @Test
    fun isFollowingCallsFollowRepository() = runTest {
        advanceUntilIdle()

        val mockUser: User = mock()
        viewModel.isFollowing(mockUser)
        advanceUntilIdle()

        verify(mockFollowRepository).doesFollow(mockUser)
    }
}