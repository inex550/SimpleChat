package com.example.simplechat.screens.chats.data.mapper

import android.content.Context
import com.example.simplechat.R
import com.example.simplechat.core.coreapi.common.preference.UserPreferenceStorage
import com.example.simplechat.screens.chats.data.models.ChatNet
import com.example.simplechat.screens.chats.domain.models.Chat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ChatsMapper @Inject constructor(
    private val userPreferenceStorage: UserPreferenceStorage,
    @ApplicationContext private val context: Context
) {

    fun map(chats: List<ChatNet>): List<Chat> = chats.map { chat -> mapChat(chat) }

    fun mapChat(chat: ChatNet): Chat =
        if (chat.users.size == 2) {
            val user = chat.users.firstOrNull { user -> user.id != userPreferenceStorage.id }

            Chat(
                id = chat.id,
                name = chat.name ?: user?.username ?: context.getString(R.string.chats_no_name),
                user = user
            )
        }
        else
            Chat(
                id = chat.id,
                name = chat.name ?: context.getString(R.string.chats_no_name),
                users = chat.users
            )
}