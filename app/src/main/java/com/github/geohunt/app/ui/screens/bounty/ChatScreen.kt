package com.github.geohunt.app.ui.screens.bounty

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.material.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.geohunt.app.model.Message
import com.github.geohunt.app.model.User
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = viewModel(factory = ChatViewModel.factory(bountyId = "98d755ad-NVP5y7V0SyObpqi226o"))
) {

    val messageState = viewModel.messages.collectAsStateWithLifecycle()
    var inputValue by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(bottom = 20.dp),) {
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
                        modifier = Modifier.fillMaxWidth(),
                        reverseLayout = true,
                    ) {
                        items(messageItems) { message ->
                            MessageCard(
                                message,
                                viewModel::getUser,
                                viewModel.isMessageMine(message)
                            )
                        }
                    }
                }
            }
        }
        Row (modifier = Modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween) {
            TextField(
                modifier = Modifier.weight(1f),
                value = inputValue,
                onValueChange = { inputValue = it },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            )
            Button(
                modifier = Modifier.height(56.dp),
                onClick = { viewModel.sendMessage(inputValue); inputValue = ""},
                enabled = inputValue.isNotBlank(),
            ) {
                Text("Send")
            }
        }
    }
}

@Composable
fun MessageCard(
    messageItem: Message,
    getUser : (Message) -> StateFlow<User?>,
    isMessageMine: Boolean?
) {
    val user = getUser(messageItem).collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = when (isMessageMine) {
            true -> Alignment.End
            else -> Alignment.Start
        }
    ) {
            Card(
                modifier = Modifier
                    .widthIn(max = 340.dp),
                shape = cardShapeFor(isMessageMine),
                backgroundColor = if (isMessageMine == true) Color.Blue else Color.LightGray
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = messageItem.content,
                    color = when (isMessageMine) {
                        true -> Color.White
                        else -> Color.Black
                    },
                )
            }
        if (user.value != null) {
            Text(
                text = user.value!!.name,
                fontSize = 12.sp,
            )
        }
        }
}

@Composable
fun cardShapeFor(isMessageMine: Boolean?): Shape {
    val roundedCorners = RoundedCornerShape(16.dp)
    return when (isMessageMine) {
        true -> roundedCorners.copy(bottomEnd = CornerSize(0))
        else -> roundedCorners.copy(bottomStart = CornerSize(0))
    }
}