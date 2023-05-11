package com.github.geohunt.app.ui.screens.home

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import com.github.geohunt.app.domain.GetUserFeedUseCase
import com.github.geohunt.app.mocks.MockAuthRepository
import com.github.geohunt.app.mocks.MockBountiesRepositories
import com.github.geohunt.app.mocks.MockChallengeRepository
import com.github.geohunt.app.mocks.MockFollowRepository
import com.github.geohunt.app.mocks.MockUserRepository
import com.github.geohunt.app.model.Bounty
import com.github.geohunt.app.model.Location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

class HomeBountiesTest {

    @get:Rule val c = createComposeRule()

    private fun createViewModel(bounties: List<Bounty>? = listOf()): HomeViewModel {
        return object: HomeViewModel(
            MockAuthRepository(),
            MockUserRepository(),
            GetUserFeedUseCase(MockAuthRepository(), MockChallengeRepository(), MockFollowRepository()),
            MockChallengeRepository(),
            MockBountiesRepositories()
        ) {
            override val bountyList = MutableStateFlow(bounties).asStateFlow()

            override fun fetchChallengeFeed() { }

            override fun refreshBounties() { }
        }
    }

    @Test
    fun showsLoadingBounties() {
        c.setContent {
            HomeBounties(vm = createViewModel(bounties = null), navigate = {})
        }
        c.onNodeWithTag("loading-bounties").assertIsDisplayed()
    }

    @Test
    fun showsEmptyBountiesMessageIfNoBounties() {
        c.setContent {
            HomeBounties(vm = createViewModel(bounties = listOf()), navigate = {})
        }
        c.onNodeWithTag("no-bounties").assertIsDisplayed()
    }

    @Test
    fun showsAllBounties() {
        c.setContent {
            HomeBounties(vm =
                createViewModel(
                    listOf(
                        Bounty("1", "1", LocalDateTime.MIN, LocalDateTime.MAX, Location(0.0, 0.0)),
                        Bounty("1", "1", LocalDateTime.MIN, LocalDateTime.MAX, Location(0.0, 0.0))
                    )
                )
            ) {}
        }
        c.onAllNodesWithTag("bounty-card").assertCountEquals(2)
    }
}