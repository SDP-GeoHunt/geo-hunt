package com.github.geohunt.app.ui.screens.bounty

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.geohunt.app.data.repository.AppContainer
import com.github.geohunt.app.data.repository.UserRepository
import com.github.geohunt.app.data.repository.bounties.*
import com.github.geohunt.app.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class ChatViewModel(
    private val teamsRepository: TeamsRepositoryInterface,
    private val messagesRepository: MessagesRepositoryInterface,
    private val userRepository: UserRepository
): ViewModel() {

    private val _messages: MutableStateFlow<List<Message>?> = MutableStateFlow(null)
    val messages: StateFlow<List<Message>?> = _messages.asStateFlow()

    init {
        fetchMessages()
    }

    private fun fetchMessages() {
        viewModelScope.launch {
            val teamId = teamsRepository.getUserTeamAsync().teamId
            messagesRepository.getTeamMessages(teamId).collect {
                _messages.value = it
            }
        }
    }

     fun sendMessage(content: String) {
         viewModelScope.launch {
             val teamId = teamsRepository.getUserTeamAsync().teamId
             val senderUid = userRepository.getCurrentUser().id
             val timestamp = System.currentTimeMillis()
             val message = Message(UUID.randomUUID().toString(), senderUid, timestamp, content)
             messagesRepository.sendMessage(teamId, message)
         }
    }

    companion object {
        fun factory(bountyId: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                val container = AppContainer.getInstance(application)

                ChatViewModel(
                    container.bounties.getTeamRepository(bountyId),
                    container.bounties.getMessageRepository(bountyId),
                    container.user,
                )
            }
        }
    }
}