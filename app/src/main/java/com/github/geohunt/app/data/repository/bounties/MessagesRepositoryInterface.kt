package com.github.geohunt.app.data.repository.bounties

import com.github.geohunt.app.model.Message
import kotlinx.coroutines.flow.Flow

interface MessagesRepositoryInterface {

    /**
    * Sends message to a team
    *
    * @param teamId The id of the team to get the messages of
    */
    fun getTeamMessages(teamId: String): Flow<List<Message>>

    /**
     * Sends message to a team
     *
     * @param teamId The id of the team to send the message to
     * @param message The message to send
     */
    suspend fun sendMessage(teamId: String, message: Message): Message
}