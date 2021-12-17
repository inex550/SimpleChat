package com.example.simplechat.services.updates.models

import android.content.Context
import com.example.simplechat.screens.chat.domain.models.Message
import com.example.simplechat.screens.chats.data.models.ChatNet
import com.example.simplechat.screens.chats.domain.models.Chat

data class UpdateNet(
    val message: Message? = null,
    val chat: ChatNet? = null,
    val type: UpdateType
) {

    fun transform(context: Context, currentUserId: String): Update = Update(
        message = message,
        chat = chat?.transform(context, currentUserId),
        type = type
    )
}