package com.github.geohunt.app.mocks

import com.github.geohunt.app.data.repository.bounties.MessagesRepositoryInterface
import com.github.geohunt.app.model.Message
import com.github.geohunt.app.model.Team
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockMessageRepository(private val messages: List<Message> = listOf()): MessagesRepositoryInterface {

    override fun getTeamMessages(teamId: String): Flow<List<Message>> {
        return flowOf(messages)
    }

    override suspend fun sendMessage(teamId: String, message: String): Message {
        return messages[0]
    }
}