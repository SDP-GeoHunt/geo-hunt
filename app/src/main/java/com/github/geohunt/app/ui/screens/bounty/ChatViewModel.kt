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
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Message
import com.github.geohunt.app.model.User
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

    private val userCache: MutableMap<Message, MutableStateFlow<User?>> = mutableMapOf()

//    private val _isMessageMine: MutableStateFlow<Boolean?> = MutableStateFlow(null)
//    val isMessageMine: StateFlow<Boolean?> = _isMessageMine

    private var currentUserId = ""

    init {
        fetchMessages()
        getCurrentUserId()
    }

    private fun fetchMessages() {
        viewModelScope.launch {
            val teamId = teamsRepository.getUserTeamAsync().teamId
            messagesRepository.getTeamMessages(teamId).collect {
                _messages.value = it
            }
        }
    }

    private fun getCurrentUserId() {
        viewModelScope.launch {
            currentUserId = userRepository.getCurrentUser().id
        }
    }

    fun isMessageMine(message: Message) : Boolean {
        return message.senderUid == currentUserId
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

    fun getUser(message: Message) : StateFlow<User?> {
        if (!userCache.contains(message)) {
            userCache[message] = MutableStateFlow(null)
            viewModelScope.launch {
                userCache[message]!!.value = userRepository.getUser(message.senderUid)
            }
        }

        return userCache[message]!!.asStateFlow()
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