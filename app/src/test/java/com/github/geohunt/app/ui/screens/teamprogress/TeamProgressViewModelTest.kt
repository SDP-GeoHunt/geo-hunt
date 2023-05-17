package com.github.geohunt.app.ui.screens.teamprogress

import com.github.geohunt.app.data.repository.AuthRepository
import com.github.geohunt.app.data.repository.ChallengeRepository
import com.github.geohunt.app.data.repository.LocationRepository
import com.github.geohunt.app.data.repository.bounties.BountiesRepository
import com.github.geohunt.app.data.repository.bounties.BountyClaimRepository
import com.github.geohunt.app.data.repository.bounties.TeamsRepository
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.model.Team
import com.github.geohunt.app.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class TeamProgressViewModelTest {
    private val fakeUser: User = User(
        id = "testUser",
        displayName = "Test user",
        profilePictureUrl = null
    )

    private val fakeTeam: Team = Team(
        teamId = "testTeam",
        name = "Test team",
        membersUid = listOf("a", "b", "c"),
        leaderUid = "a",
        score = 0
    )

    private val fakeChallenge = Challenge(
        id = "testChallenge",
        authorId = "a",
        photoUrl = "",
        location = Location(0.0, 0.0),
        publishedDate = LocalDateTime.now(),
        expirationDate = null,
        difficulty = Challenge.Difficulty.MEDIUM,
        description = null
    )

    private val mockAuth: AuthRepository = mock {
        on { isLoggedIn() } doReturn true
        on { getCurrentUser() } doReturn fakeUser
    }

    private lateinit var viewModel: TeamProgressViewModel

    private val mockLocationRepository: LocationRepository = mock {
        on { getLocations(any()) } doReturn flowOf(Location(0.0, 0.0))
    }

    private val mockTeamRepository: TeamsRepository = mock {
        onBlocking { getUserTeamAsync() } doReturn fakeTeam
    }
    private val mockChallengeRepository: ChallengeRepository = mock {
        onBlocking { getChallenges() } doReturn listOf(fakeChallenge)
    }
    private val mockClaimRepository: BountyClaimRepository = mock()

    private val mockBountiesRepository: BountiesRepository = mock {
        on { getTeamRepository(any<String>()) } doReturn mockTeamRepository
        on { getChallengeRepository(any<String>()) } doReturn mockChallengeRepository
        on { getClaimRepository(any<String>()) } doReturn mockClaimRepository
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())

        viewModel = TeamProgressViewModel(
            authRepository = mockAuth,
            userRepository = mock(),
            activeHuntsRepository = mock(),
            locationRepository = mockLocationRepository,
            bountiesRepository = mockBountiesRepository,
            bountyId = ""
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun challengesAreFetchedOnInit() = runTest {
        advanceUntilIdle()
        verify(mockChallengeRepository).getChallenges()
    }

    @Test
    fun teamIsFetchedOnInit() = runTest {
        advanceUntilIdle()
        verify(mockTeamRepository).getUserTeamAsync()
    }

    @Test
    fun teamStatusIsTeamLoadedOnSuccess() = runTest {
        advanceUntilIdle()
        assertThat(viewModel.teamStatus.value, `is`(TeamProgressViewModel.TeamStatus.LOADED_TEAM))
    }

    @Test
    fun locationIsCollectedOnInit() = runTest {
        advanceUntilIdle()
        verify(mockLocationRepository).getLocations(any())
    }
}