package com.github.geohunt.app.ui.screens.home

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import com.github.geohunt.app.mocks.MockAuthRepository
import com.github.geohunt.app.mocks.MockBountiesRepositories
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

    private fun createViewModel(bounties: List<Bounty>? = listOf()): BountyFeedViewModel {
        return object: BountyFeedViewModel(
            MockAuthRepository(),
            MockUserRepository(),
            MockBountiesRepositories()
        ) {
            override val bountyList = MutableStateFlow(bounties).asStateFlow()

            override suspend fun fetchBounties() {}
        }
    }

    @Test
    fun showsLoadingBounties() {
        c.setContent {
            BountiesFeed(viewModel = createViewModel(bounties = null), showTeamChooser = {}, showTeamProgress = {}, onUserClick = {})
        }
        c.onNodeWithTag("loading-bounties").assertIsDisplayed()
    }

    @Test
    fun showsEmptyBountiesMessageIfNoBounties() {
        c.setContent {
            BountiesFeed(viewModel = createViewModel(bounties = listOf()), showTeamChooser = {}, showTeamProgress = {},
            onUserClick = {})
        }
        c.onNodeWithTag("no-bounties").assertIsDisplayed()
    }

    @Test
    fun showsAllBounties() {
        c.setContent {
            BountiesFeed(viewModel =
                createViewModel(
                    listOf(
                        Bounty("1", "1", "1", LocalDateTime.MIN, LocalDateTime.MAX, Location(0.0, 0.0)),
                        Bounty("1", "1", "1", LocalDateTime.MIN, LocalDateTime.MAX, Location(0.0, 0.0))
                    )
                ),
                showTeamChooser = {},
                showTeamProgress = {},
                onUserClick = {}
            )
        }
        c.onAllNodesWithTag("bounty-card").assertCountEquals(2)
    }
}