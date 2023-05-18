package com.github.geohunt.app.ui.screens.teamprogress

import android.app.Application
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.geohunt.app.data.repository.AppContainer
import com.github.geohunt.app.data.repository.ImageRepository
import com.github.geohunt.app.data.repository.LocationRepositoryInterface
import com.github.geohunt.app.data.repository.bounties.BountiesRepository
import com.github.geohunt.app.mocks.MockAuthRepository
import com.github.geohunt.app.mocks.MockUserRepository
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.model.database.FirebaseEmulator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalTestApi::class, ExperimentalCoroutinesApi::class)
class TeamProgressScreenTest {
    @get:Rule
    val testRule = createComposeRule()

    // Keys in the emulated RTDB
    private val bountyId = "testBountyId"

    private val testFactory = viewModelFactory {
        initializer {
            FirebaseEmulator.init()
            val mockAuth = MockAuthRepository()
            val mockUserRepo = MockUserRepository(mockAuth = mockAuth)

            val mockLocation = Location(41.5, -0.5)
            val mockLocationRepo = object : LocationRepositoryInterface {
                override fun getLocations(coroutineScope: CoroutineScope): Flow<Location> {
                    return flowOf(mockLocation)
                }
            }

            val storage = FirebaseEmulator.getEmulatedStorage()

            val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
            val container = AppContainer.getEmulatedFirebaseInstance(application)

            val bountiesRepository = BountiesRepository(
                userRepository = mockUserRepo,
                authRepository = mockAuth,
                database = FirebaseEmulator.getEmulatedFirebase(),
                storage = storage,
                imageRepository = ImageRepository(storage),
                ioDispatcher = UnconfinedTestDispatcher()
            )

            TeamProgressViewModel(
                authRepository = mockAuth,
                userRepository = mockUserRepo,
                locationRepository = mockLocationRepo,
                bountiesRepository = bountiesRepository,
                bountyId = bountyId
            )
        }
    }

    @Test
    fun onBackIsCalledOnBackButton() {
        var isBackCalled = false

        testRule.setContent {
            TeamProgressScreen(
                onBack = { isBackCalled = true },
                onLeaderboard = {},
                onChat = {},
                bountyId = bountyId,
                viewModel = viewModel(factory = testFactory)
            )
        }
        testRule.waitForIdle()

        val backButton = testRule.onNodeWithContentDescription("Back button")
        backButton.assertExists()
        backButton.assertHasClickAction()
        backButton.performClick()

        assert(isBackCalled)
    }

    @Test
    fun onLeaderboardClickOpensLeaderboard() {
        var isLeaderboardOpen = false

        testRule.setContent {
            TeamProgressScreen(
                onBack = {},
                onLeaderboard = { isLeaderboardOpen = true },
                onChat = {},
                bountyId = bountyId,
                viewModel = viewModel(factory = testFactory)
            )
        }
        testRule.waitUntilDoesNotExist(hasText("Loading", substring = true), timeoutMillis = DEFAULT_TIMEOUT)

        val leaderboardButton = testRule.onNodeWithTag("Open leaderboard", useUnmergedTree = true)
        leaderboardButton.assertExists()
        leaderboardButton.assertHasClickAction()
        leaderboardButton.performClick()

        assert(isLeaderboardOpen)
    }

    @Test
    fun onChatClickOpensChat() {
        var isChatOpened = false

        testRule.setContent {
            TeamProgressScreen(
                onBack = { },
                onLeaderboard = {},
                onChat = { isChatOpened = true },
                bountyId = bountyId,
                viewModel = viewModel(factory = testFactory)
            )
        }
        testRule.waitUntilDoesNotExist(hasText("Loading", substring = true), timeoutMillis = DEFAULT_TIMEOUT)

        val openChatButton = testRule.onNodeWithTag("Open chat", useUnmergedTree = true)
        openChatButton.assertExists()
        openChatButton.assertHasClickAction()
        openChatButton.performClick()

        assert(isChatOpened)
    }

    @Test
    fun displaysTeamMembers() {
        testRule.setContent {
            TeamProgressScreen(
                onBack = {},
                onLeaderboard = {},
                onChat = {},
                bountyId = bountyId,
                viewModel = viewModel(factory = testFactory)
            )
        }
        testRule.waitUntilDoesNotExist(hasText("Loading", substring = true), timeoutMillis = DEFAULT_TIMEOUT)

        testRule.waitUntilExactlyOneExists(hasText("dn"), timeoutMillis = DEFAULT_TIMEOUT)
    }

    @Test
    fun displaysChallenges() {
        testRule.setContent {
            TeamProgressScreen(
                onBack = {},
                onLeaderboard = {},
                onChat = {},
                bountyId = bountyId,
                viewModel = viewModel(factory = testFactory)
            )
        }
        testRule.waitUntilDoesNotExist(hasText("Loading", substring = true), timeoutMillis = DEFAULT_TIMEOUT)
        testRule.waitUntilDoesNotExist(hasTestTag("loadingChallenges"), timeoutMillis = DEFAULT_TIMEOUT)

        // Check if location is displayed
        testRule.waitUntilExactlyOneExists(hasText("km", substring = true), timeoutMillis = DEFAULT_TIMEOUT)

        // Check if the button is shown
        val button = testRule.onNodeWithText("Claim", useUnmergedTree = true)
        button.assertExists()
        button.onParent().assertHasClickAction()
    }

    companion object {
        private const val DEFAULT_TIMEOUT = 10_000L
    }
}