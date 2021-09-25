package com.example.simplechat.screens.chats.domain.usecase

import com.example.simplechat.screens.chats.domain.models.Chat
import com.example.simplechat.screens.chats.domain.repository.ChatsRepository
import javax.inject.Inject

class CreatePrivateChatUseCase @Inject constructor(
    private val repository: ChatsRepository
) {

    suspend fun invoke(username: String): Chat =
        repository.createPrivateChat(username)
}