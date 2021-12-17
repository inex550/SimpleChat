package com.example.simplechat.services.updates.models

import com.example.simplechat.screens.chat.domain.models.Message
import com.example.simplechat.screens.chats.domain.models.Chat

data class Update(
    val message: Message?,
    val chat: Chat?,
    val type: UpdateType,
)