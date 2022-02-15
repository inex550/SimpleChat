package com.example.simplechat.screens.chats.data.repository

import com.example.simplechat.core.network.apiservice.ApiService
import com.example.simplechat.screens.chats.data.mapper.ChatsMapper
import com.example.simplechat.screens.chats.data.models.NewPrivateChat
import com.example.simplechat.screens.chats.domain.models.Chat
import com.example.simplechat.screens.chats.domain.repository.ChatsRepository
import javax.inject.Inject

class ChatsRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val chatsMapper: ChatsMapper
): ChatsRepository {

    override suspend fun getMyChats(): List<Chat> =
        chatsMapper.map(apiService.getMyChats())

    override suspend fun createPrivateChat(username: String): Chat =
        chatsMapper.mapChat(apiService.createPrivateChat(NewPrivateChat(
            username = username
        )))

    override suspend fun deleteChat(chatId: Int) {
        apiService.deleteChat(chatId)
    }
}