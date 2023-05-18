package com.github.geohunt.app.data.repository.bounties

import com.github.geohunt.app.data.repository.UserRepository
import com.github.geohunt.app.data.repository.UserRepositoryInterface
import com.github.geohunt.app.model.Message
import com.github.geohunt.app.model.Team
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.snapshots
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MessagesRepository(
    bountyReference: DatabaseReference,
    private val userRepository: UserRepositoryInterface,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): MessagesRepositoryInterface {
    private val messages = bountyReference.child("messages")

    override fun getTeamMessages(teamId: String): Flow<List<Message>> {
        return messages.child(teamId).orderByKey().snapshots
            .map { it -> it.children.map { snapshotToMessage(it) } }
            .flowOn(ioDispatcher)
    }

    override suspend fun sendMessage(teamId: String, message: String) : Message {
        return withContext(ioDispatcher) {
            val newMessageReference = messages.push()
            val messageId = newMessageReference.key!!
            val senderUid = userRepository.getCurrentUser().id
            val timestamp = System.currentTimeMillis()
            val messageData = Message(messageId, senderUid, timestamp, message)
            messages.child(teamId).child(messageId).setValue(messageData).await()
            messageData
        }

    }

    private fun snapshotToMessage(s: DataSnapshot): Message {
        return Message(
            messageId = s.key!!,
            senderUid = s.child("senderUid").getValue(String::class.java)!!,
            timestamp = s.child("timestamp").getValue(Long::class.java)!!,
            content = s.child("content").getValue(String::class.java)!!
        )
    }
}