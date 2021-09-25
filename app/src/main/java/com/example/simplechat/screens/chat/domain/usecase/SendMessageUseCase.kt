package com.example.simplechat.screens.chat.domain.usecase

import com.example.simplechat.screens.chat.domain.models.Message
import com.example.simplechat.screens.chat.domain.repository.ChatRepository
import com.example.simplechat.screens.chats.domain.models.Chat
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val repository: ChatRepository
) {

    suspend fun invoke(chatId: Int, text: String): Message =
        repository.sendMessage(chatId, text)
}