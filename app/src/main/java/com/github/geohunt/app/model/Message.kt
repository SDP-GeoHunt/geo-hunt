package com.github.geohunt.app.model

data class Message(
    val messageId: String,
    val senderUid: String,
    val timestamp: Long,
    val content: String,
)