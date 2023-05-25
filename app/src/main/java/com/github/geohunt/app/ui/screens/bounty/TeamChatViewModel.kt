package com.github.geohunt.app.ui.screens.bounty

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.geohunt.app.data.repository.AppContainer
import com.github.geohunt.app.data.repository.UserRepository
import com.github.geohunt.app.data.repository.UserRepositoryInterface
import com.github.geohunt.app.data.repository.bounties.*
import com.github.geohunt.app.model.Message
import com.github.geohunt.app.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*

open class TeamChatViewModel(
    private val teamsRepository: TeamsRepositoryInterface,
    private val messagesRepository: MessagesRepositoryInterface,
    private val userRepository: UserRepositoryInterface
): ViewModel() {

    private val _messages: MutableStateFlow<List<Message>?> = MutableStateFlow(null)
    val messages: StateFlow<List<Message>?> = _messages.asStateFlow()

    private val userCache: MutableMap<String, MutableStateFlow<User?>> = mutableMapOf()

    private var currentUserId = ""

    init {
        fetchMessages()
        getCurrentUserId()
    }

    private fun fetchMessages() {
        viewModelScope.launch {
            val teamId = teamsRepository.getUserTeam().first()?.teamId
            if (teamId != null) {
                messagesRepository.getTeamMessages(teamId).collect {
                    _messages.value = it
                }
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

     open fun sendMessage(content: String) {
         viewModelScope.launch {
             val teamId = teamsRepository.getUserTeam().first()?.teamId
             if (teamId != null) {
                 messagesRepository.sendMessage(teamId, content)
             }
         }
    }

    fun getUser(senderUid: String) : StateFlow<User?> {
        if (!userCache.contains(senderUid)) {
            userCache[senderUid] = MutableStateFlow(null)
            viewModelScope.launch {
                userCache[senderUid]!!.value = userRepository.getUser(senderUid)
            }
        }

        return userCache[senderUid]!!.asStateFlow()
    }

    companion object {
        fun factory(bountyId: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                val container = AppContainer.getInstance(application)

                TeamChatViewModel(
                    container.bounty.getTeamRepository(bountyId),
                    container.bounty.getMessageRepository(bountyId),
                    container.user,
                )
            }
        }
    }
}