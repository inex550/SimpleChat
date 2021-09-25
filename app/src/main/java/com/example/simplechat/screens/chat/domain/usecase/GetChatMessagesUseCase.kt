package com.example.simplechat.screens.chat.domain.usecase

import com.example.simplechat.screens.chat.domain.models.Message
import com.example.simplechat.screens.chat.domain.repository.ChatRepository
import javax.inject.Inject

class GetChatMessagesUseCase @Inject constructor(
    private val repository: ChatRepository
) {

    suspend fun invoke(chatId: Int, start: Int? = null, batch: Int? = 20): List<Message> =
        repository.getChatMessages(chatId, start, batch)
}