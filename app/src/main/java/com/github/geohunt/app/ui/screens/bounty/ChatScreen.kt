package com.github.geohunt.app.ui.screens.bounty

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: ChatViewModel = viewModel(factory = ChatViewModel.Factory)) {
    val messages = viewModel.messages.collectAsStateWithLifecycle()
    var messageContent by remember { mutableStateOf("") }

    Column (
        modifier = Modifier.fillMaxSize()
    ) {
        when (messages.value) {
            null -> Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            else ->
                LazyColumn {
                    items(messages.value!!) { message ->
                        Text(text = "${message.senderUid}: ${message.content}")
                    }
                }
        }
        TextField(
            value = messageContent,
            onValueChange = { messageContent = it },
            label = { Text("Message") }
        )
        Button (onClick = { viewModel.sendMessage(messageContent) }) {
            Text("Send")
        }
    }
}