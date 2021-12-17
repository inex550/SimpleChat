package com.example.simplechat.screens.chat.domain.repository

import com.example.simplechat.screens.chat.domain.models.Message
import com.example.simplechat.services.updates.models.UpdateNet

interface ChatRepository {

    suspend fun sendMessage(chatId: Int, text: String): Message

    suspend fun pollingUpdates(): List<UpdateNet>

    suspend fun getChatMessages(chatId: Int, start: Int? = null, batch: Int? = 20): List<Message>
}