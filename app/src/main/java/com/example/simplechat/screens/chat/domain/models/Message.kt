package com.example.simplechat.screens.chat.domain.models

import com.example.simplechat.screens.chats.domain.models.User
import com.google.gson.annotations.SerializedName

data class Message(
    val id: Int,
    val text: String,

    @SerializedName("sender_id")
    val senderId: Int,

    val sender: User,

    @SerializedName("chat_id")
    val chatId: Int,
) {
    constructor(): this(-1, "", -1, User("", "", null), -1)
}
