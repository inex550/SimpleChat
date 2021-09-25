package com.example.simplechat.screens.chat.domain.repository

import com.example.simplechat.screens.chat.domain.models.Message
import com.example.simplechat.screens.chat.domain.models.SendMessage
import com.example.simplechat.screens.chat.domain.models.Update

interface ChatRepository {

    suspend fun sendMessage(chatId: Int, text: String): Message

    suspend fun pollingUpdates(): List<Update>

    suspend fun getChatMessages(chatId: Int, start: Int? = null, batch: Int? = 20): List<Message>
}