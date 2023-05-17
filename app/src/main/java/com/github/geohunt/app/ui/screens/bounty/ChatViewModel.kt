package com.github.geohunt.app.ui.screens.bounty

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.geohunt.app.data.repository.AppContainer
import com.github.geohunt.app.data.repository.UserRepository
import com.github.geohunt.app.data.repository.bounties.MessagesRepository
import com.github.geohunt.app.data.repository.bounties.TeamsRepository
import com.github.geohunt.app.model.Message
import com.github.geohunt.app.ui.screens.home.HomeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class ChatViewModel(
    private val teamId: String,
    private val teamsRepository: TeamsRepository,
    private val messagesRepository: MessagesRepository,
    private val userRepository: UserRepository
): ViewModel() {

    private val _messages: MutableStateFlow<List<Message>?> = MutableStateFlow(null)
    val messages: StateFlow<List<Message>?> = _messages.asStateFlow()

    init {
        fetchMessages()
    }

    private fun fetchMessages() {
        viewModelScope.launch {
            messagesRepository.getTeamMessages(teamId).collect {
                _messages.value = it
            }
        }
    }

    suspend fun sendMessage(content: String) {
        val senderUid = userRepository.getCurrentUser().id // assuming there is a getCurrentUser() method
        val timestamp = System.currentTimeMillis()
        val message = Message(UUID.randomUUID().toString(), senderUid, timestamp, content)
        messagesRepository.sendMessage(teamId, message)
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                val container = AppContainer.getInstance(application)
                val teamId = container.
                ChatViewModel(
                    container.auth,
                    container.feedUseCase,
                    container.challenges
                )
            }
        }
    }
}