package com.github.geohunt.app.ui.screens.bounty

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun ChatScreen(viewModel: ChatViewModel) {
    val messages by viewModel.messages.observeAsState(initial = emptyList())

    Column {
        LazyColumn {
            items(messages) { message ->
                Text(text = "${message.timestamp}: ${message.content}") // adjust this to include sender info or other format as needed
            }
        }

        var messageContent by remember { mutableStateOf("") }
        TextField(
            value = messageContent,
            onValueChange = { messageContent = it },
            label = { Text("Message") }
        )
        Button(onClick = { viewModel.sendMessage(messageContent) }) {
            Text("Send")
        }
    }
}