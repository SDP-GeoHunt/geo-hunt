package com.github.geohunt.app.ui.screens.teamchat

import com.github.geohunt.app.data.repository.UserRepository
import com.github.geohunt.app.data.repository.bounties.MessagesRepository
import com.github.geohunt.app.data.repository.bounties.TeamsRepository
import com.github.geohunt.app.model.Message
import com.github.geohunt.app.model.Team
import com.github.geohunt.app.model.User
import com.github.geohunt.app.ui.screens.bounty.TeamChatViewModel
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class TeamChatViewModelTest {
    private lateinit var viewModel: TeamChatViewModel

    private val fakeTeam: Team = Team(
        teamId = "testTeam",
        name = "Test team",
        membersUid = listOf("a", "b", "c"),
        leaderUid = "a",
        score = 0
    )

    private val fakeMessage1: Message = Message(
        messageId = "mockId1",
        senderUid = "mockUserId",
        timestamp = 1684452526537,
        content = "dummy message 1"
    )

    private val fakeMessage2: Message = Message(
        messageId = "mockId1",
        senderUid = "mockWrongUserId",
        timestamp = 1684452526537,
        content = "dummy message 1"
    )

    private val fakeUser: User = User(
        id = "mockUserId",
        displayName = "mockDisplayName",
        profilePictureUrl = "mockUrl",
    )

    private val mockTeamRepository: TeamsRepository = mock {
        onBlocking { getUserTeam() } doReturn flowOf(fakeTeam)
    }

    private val mockMessageRepository: MessagesRepository = mock {
        onBlocking { sendMessage(any(), any()) } doReturn fakeMessage1
        on { getTeamMessages(any()) } doReturn flowOf(listOf(fakeMessage1))
    }

    private val mockUserRepository: UserRepository = mock {
        onBlocking { getCurrentUser() } doReturn fakeUser
        onBlocking { getUser(any()) } doReturn fakeUser
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())

        viewModel = TeamChatViewModel(
            teamsRepository = mockTeamRepository,
            messagesRepository = mockMessageRepository,
            userRepository = mockUserRepository,
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun currentTeamIsFetchedOnInit() = runTest {
        advanceUntilIdle()
        verify(mockTeamRepository).getUserTeam()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun messagesAreFetchedOnInit() = runTest {
        advanceUntilIdle()
        verify(mockMessageRepository).getTeamMessages(any())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun currentUserIsFetchedOnInit() = runTest {
        advanceUntilIdle()
        verify(mockUserRepository).getCurrentUser()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun checkMessageIsMine() = runTest {
        advanceUntilIdle()
        assert(viewModel.isMessageMine(fakeMessage1))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun checkMessageIsNotMine() = runTest {
        advanceUntilIdle()
        assertFalse(viewModel.isMessageMine(fakeMessage2))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun sendMessageCalled() = runTest {
        viewModel.sendMessage(fakeMessage1.content)
        advanceUntilIdle()
        verify(mockMessageRepository).sendMessage(fakeTeam.teamId, fakeMessage1.content)
    }

}