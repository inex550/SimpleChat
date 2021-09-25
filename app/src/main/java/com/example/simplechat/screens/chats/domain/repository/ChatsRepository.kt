package com.example.simplechat.screens.chats.domain.repository

import com.example.simplechat.screens.chats.data.models.ChatNet
import com.example.simplechat.screens.chats.domain.models.Chat

interface ChatsRepository {

    suspend fun getMyChats(): List<Chat>

    suspend fun createPrivateChat(username: String): Chat

    suspend fun deleteChat(chatId: Int)
}