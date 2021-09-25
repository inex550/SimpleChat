package com.example.simplechat.screens.chat.data.repository

import com.example.simplechat.core.coreimpl.network.apiservice.ApiService
import com.example.simplechat.screens.chat.domain.models.Message
import com.example.simplechat.screens.chat.domain.models.SendMessage
import com.example.simplechat.screens.chat.domain.models.Update
import com.example.simplechat.screens.chat.domain.repository.ChatRepository
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): ChatRepository {

    override suspend fun sendMessage(chatId: Int, text: String): Message =
        apiService.sendMessage(chatId, SendMessage(text))

    override suspend fun pollingUpdates(): List<Update> =
        apiService.getUpdates()

    override suspend fun getChatMessages(chatId: Int, start: Int?, batch: Int?): List<Message> =
        apiService.getChatMessages(chatId, start, batch)
}