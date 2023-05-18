package com.github.geohunt.app.ui.screens.bounty

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.geohunt.app.model.Message
import kotlinx.coroutines.flow.StateFlow

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ChatScreen(
//    bountyId: String = "98d755ad-NVP5y7V0SyObpqi226o",
//    viewModel: ChatViewModel = viewModel(factory = ChatViewModel.factory(bountyId = "98d755ad-NVP5y7V0SyObpqi226o"))
//) {
//    val messages = viewModel.messages.collectAsStateWithLifecycle()
//    var messageContent by remember { mutableStateOf("") }
//
//    Column(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        when (messages.value) {
//            null -> Box(modifier = Modifier.fillMaxSize()) {
//                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//            }
//            else ->
//                LazyColumn {
//                    items(messages.value!!) { message ->
//                        Text(text = "${viewModel.getUserName(message.senderUid)}: ${message.content}")
//                    }
//                }
//        }
//        TextField(
//            value = messageContent,
//            onValueChange = { messageContent = it },
//            label = { Text("Message") }
//        )
//        Button(onClick = { viewModel.sendMessage(messageContent) }) {
//            Text("Send")
//        }
//    }
//}

@Composable
fun ChatScreen(
    cid: String,
) {
    Column(Modifier.fillMaxSize()) {
        val factory = MessageListViewModelFactory(cid)

        MessageList(
            factory = factory,
            modifier = Modifier.weight(1f),
        )
        MessageInput(factory = factory)
    }
}

@Composable
fun MessageList(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = viewModel(factory = ChatViewModel.factory(bountyId = "98d755ad-NVP5y7V0SyObpqi226o"))
) {

    val messageState = viewModel.messages.collectAsStateWithLifecycle()

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (messageState.value) {
            null -> Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            else -> {
                val messageItems = messageState.value!!.asReversed()
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    reverseLayout = true,
                ) {
                    items(messageItems) { message ->
                        MessageCard(
                            message,
                            viewModel.getUserName(message.senderUid),
                            viewModel.isMessageMine(message)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MessageCard(
    messageItem: Message,
    userName: String,
    isMessageMine: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = when {
            isMessageMine -> Alignment.End
            else -> Alignment.Start
        }
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 340.dp)
                .background(if (isMessageMine) MaterialTheme.colors.primary else MaterialTheme.colors.secondary),
            shape = cardShapeFor(isMessageMine),
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = messageItem.content,
                color = when {
                    isMessageMine -> MaterialTheme.colors.onPrimary
                    else -> MaterialTheme.colors.onSecondary
                },
            )
        }
        Text(
            text = userName,
            fontSize = 12.sp,
        )
    }
}

@Composable
fun cardShapeFor(isMessageMine: Boolean): Shape {
    val roundedCorners = RoundedCornerShape(16.dp)
    return when {
        isMessageMine -> roundedCorners.copy(bottomEnd = CornerSize(0))
        else -> roundedCorners.copy(bottomStart = CornerSize(0))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInput(
    factory: MessageListViewModelFactory,
    messageInputViewModel: MessageInputViewModel = viewModel(factory = factory),
) {
    var inputValue by remember { mutableStateOf("") }

    fun sendMessage() {
        messageInputViewModel.sendMessage(inputValue)
        inputValue = ""
    }

    Row {
        TextField(
            modifier = Modifier.weight(1f),
            value = inputValue,
            onValueChange = { inputValue = it },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions { sendMessage() },
        )
        Button(
            modifier = Modifier.height(56.dp),
            onClick = { sendMessage() },
            enabled = inputValue.isNotBlank(),
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = stringResource(R.string.cd_button_send)
            )
        }
    }
    32
}