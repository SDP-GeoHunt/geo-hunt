package com.github.geohunt.app.ui.screens.bounty

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.github.geohunt.app.data.repository.UserRepository
import com.github.geohunt.app.data.repository.bounties.MessagesRepository
import com.github.geohunt.app.data.repository.bounties.TeamsRepository
import com.github.geohunt.app.model.Message
import java.util.*

class ChatViewModel(
    private val teamId: String,
    private val teamsRepository: TeamsRepository,
    private val messagesRepository: MessagesRepository,
    private val userRepository: UserRepository
): ViewModel() {

    val messages = messagesRepository.getTeamMessages(teamId).asLiveData()

    suspend fun sendMessage(content: String) {
        val senderUid = userRepository.getCurrentUser().id // assuming there is a getCurrentUser() method
        val timestamp = System.currentTimeMillis()
        val message = Message(UUID.randomUUID().toString(), senderUid, timestamp, content)
        messagesRepository.sendMessage(teamId, message)
    }
}