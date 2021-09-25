package com.example.simplechat.screens.chats.domain.usecase

import com.example.simplechat.screens.chats.domain.repository.ChatsRepository
import javax.inject.Inject

class DeleteChatUseCase @Inject constructor(
    private val repository: ChatsRepository
) {

    suspend fun invoke(chatId: Int) = repository.deleteChat(chatId)
}