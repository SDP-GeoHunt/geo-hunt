package com.github.geohunt.app.ui.screens.bounty_team_select

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.github.geohunt.app.data.repository.ChallengeRepositoryInterface
import com.github.geohunt.app.data.repository.bounties.BountiesRepositoryInterface
import com.github.geohunt.app.mocks.MockBountiesRepositories
import com.github.geohunt.app.mocks.MockChallengeRepository
import com.github.geohunt.app.mocks.MockTeamRepository
import com.github.geohunt.app.mocks.MockUserRepository
import com.github.geohunt.app.model.Bounty
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.model.Team
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

class BountyTeamSelectPageTest {
    @get:Rule
    val c = createComposeRule()

    private val fakeBountyRepository: BountiesRepositoryInterface = object: MockBountiesRepositories() {
        override suspend fun getBountyById(bid: String): Bounty {
            if (bid == "1") return Bounty(
                "1",
                "name",
                location = Location(0.0, 0.0),
                expirationDate = LocalDateTime.MAX,
                adminUid = "1",
                startingDate = LocalDateTime.MIN
            ) else
                throw java.lang.IllegalStateException()
        }

        override fun getChallengeRepository(bountyId: String): ChallengeRepositoryInterface {
            return MockChallengeRepository()
        }
    }

    private fun createFakeViewModel(): BountyTeamSelectViewModel {
        return BountyTeamSelectViewModel(
            bountyId = "1",
            bountiesRepository = fakeBountyRepository,
            challengeRepository = fakeBountyRepository.getChallengeRepository("1"),
            teamsRepository = MockTeamRepository(listOf(
                Team("1", "caca", leaderUid = "1", membersUid = listOf("1", "2"), score = 100)
            )),
            userRepository = MockUserRepository()
        )
    }

    // Global test because we definitely not have the time to do everything
    @Test
    fun globalTest() {
        val cfBack = CompletableFuture<Void?>()
        val cfSelectedTeam = CompletableFuture<Team>()
        c.setContent {
            BountyTeamSelectPage(
                bountyId = "1",
                onBack = { cfBack.complete(null) },
                onSelectedTeam = { cfSelectedTeam.complete(it) },
                viewModel = createFakeViewModel()
            )
        }
        c.onNodeWithText("Select team for name").assertIsDisplayed()
        c.onNodeWithTag("check-btn").assertIsEnabled()

    }
}