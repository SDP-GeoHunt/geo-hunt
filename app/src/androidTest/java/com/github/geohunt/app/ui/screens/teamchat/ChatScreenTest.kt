package com.github.geohunt.app.ui.screens.teamchat

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.geohunt.app.data.repository.ChallengeRepositoryInterface
import com.github.geohunt.app.data.repository.LocationRepositoryInterface
import com.github.geohunt.app.data.repository.bounties.BountyClaimRepositoryInterface
import com.github.geohunt.app.data.repository.bounties.TeamsRepositoryInterface
import com.github.geohunt.app.mocks.*
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.model.Message
import com.github.geohunt.app.model.Team
import com.github.geohunt.app.model.database.FirebaseEmulator
import com.github.geohunt.app.ui.screens.bounty.ChatScreen
import com.github.geohunt.app.ui.screens.bounty.TeamChatViewModel
import com.github.geohunt.app.ui.screens.teamprogress.TeamProgressScreen
import com.github.geohunt.app.ui.screens.teamprogress.TeamProgressViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test

class ChatScreenTest {
    @get:Rule
    val testRule = createComposeRule()

    private val bountyId = "testBountyId"

    private val mockAuth = MockAuthRepository()
    private val mockUserRepository = MockUserRepository(mockAuth = mockAuth)

    private val mockTeamRepository = MockTeamRepository(listOf(
        Team("1", "1", score = 1, membersUid = listOf("1", "2"), leaderUid = "1")
    ))

    private val fakeMessage: Message = Message(
        messageId = "mockId1",
        senderUid = "1",
        timestamp = 1684452526537,
        content = "dummy message 1"
    )

    private val mockMessageRepository = MockMessageRepository(listOf(fakeMessage))

    private val testFactory = viewModelFactory {
        initializer {
            TeamChatViewModel(
                teamsRepository = mockTeamRepository,
                messagesRepository = mockMessageRepository,
                userRepository = mockUserRepository,
            )
        }
    }

    @Test
    fun backButtonExists() {
        testRule.setContent {
            ChatScreen(onBack = {}, bountyId = bountyId, viewModel = viewModel(
                factory = testFactory
            ))
        }

        val backButton = testRule.onNodeWithTag("back-btn")
        backButton.assertExists()
    }

    @Test
    fun messageIsDisplayedOnInit() {
        testRule.setContent {
            ChatScreen(onBack = {}, bountyId = bountyId, viewModel = viewModel(
                factory = testFactory
            ))
        }
        val messageContent = testRule.onNodeWithText(fakeMessage.content)
        val sender = testRule.onNodeWithText("dn")

        messageContent.assertExists()
        sender.assertExists()
    }

    @Test
    fun messageInputIsDisplayed() {
        testRule.setContent {
            ChatScreen(onBack = {}, bountyId = bountyId, viewModel = viewModel(
                factory = testFactory
            ))
        }
        val textInput = testRule.onNodeWithTag("message-input")
        val sendButton = testRule.onNodeWithTag("send-button")
        textInput.assertExists()
        sendButton.assertExists()
    }


}