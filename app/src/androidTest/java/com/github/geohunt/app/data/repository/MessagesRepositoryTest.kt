package com.github.geohunt.app.data.repository

import com.github.geohunt.app.data.repository.bounties.MessagesRepository
import com.github.geohunt.app.data.repository.bounties.MessagesRepositoryInterface
import com.github.geohunt.app.mocks.MockAuthRepository
import com.github.geohunt.app.mocks.MockUserRepository
import com.github.geohunt.app.mocks.mockUser
import com.github.geohunt.app.model.Message
import com.github.geohunt.app.model.database.FirebaseEmulator
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MessagesRepositoryTest {
    private lateinit var database: FirebaseDatabase
    private val teamId = "team1"
    private val user = MockUserRepository()
    private val defaultUser = MockAuthRepository.defaultLoggedUser
    private val ioDispatcher = UnconfinedTestDispatcher()
    private lateinit var bountyReference: DatabaseReference

    @Before
    fun setupEmulator() {
        database = FirebaseEmulator.getEmulatedFirebase()
        bountyReference = database.getReference("bounties")
    }

    @After
    fun clearEmulator() {
        bountyReference.child("messages").removeValue()
    }

    private fun messages(): MessagesRepositoryInterface {
        return MessagesRepository(bountyReference, user, ioDispatcher)
    }
    @Test
    fun sendMessagesUpdatesDatabase() = runTest {
        val messages = messages()

        val message = messages.sendMessage(teamId, "Yes, very good testing")

        val content = bountyReference
                .child("messages")
                .child(teamId)
                .child(message.messageId)
                .child("content")
                .get().await().getValue(String::class.java)
        assertEquals("Yes, very good testing", content)
        assertEquals(message.content, content)

        val senderUid = bountyReference
            .child("messages")
            .child(teamId)
            .child(message.messageId)
            .child("senderUid")
            .get().await().getValue(String::class.java)

        assertEquals(defaultUser.id, senderUid)
        assertEquals(message.senderUid, senderUid)

        val timestamp = bountyReference
                .child("messages")
                .child(teamId)
                .child(message.messageId)
                .child("timestamp")
                .get().await().getValue(Long::class.java)
        assertEquals(message.timestamp, timestamp)
    }

    @Test
    fun messageListenerReceivesMessages() = runTest {
        val messages = messages()
        val user2 = mockUser("user2")
        val messages2 = MessagesRepository(bountyReference, MockUserRepository(mockAuth = MockAuthRepository(user2)), ioDispatcher)

        val sentMessages = mutableListOf<Message>()
        val messageFlow = messages.getTeamMessages(teamId)

        assertEquals(sentMessages, messageFlow.first())

        val m1 = "Haha this bounty is so fun ;))"
        sentMessages.add(messages.sendMessage(teamId, m1))
        assertEquals(sentMessages, messageFlow.first())

        val m2 = "I'm having fun"
        sentMessages.add(messages.sendMessage(teamId, m2))
        assertEquals(sentMessages, messageFlow.first())

        val m3 = "Haha you're right testUser1, wait why does your name sound so weird"
        sentMessages.add(messages2.sendMessage(teamId, m3))
        assertEquals(sentMessages, messageFlow.first())

        val m4 = "Wait, are we living in a simulation ?!?"
        sentMessages.add(messages2.sendMessage(teamId, m4))
        assertEquals(sentMessages, messageFlow.first())
    }
}