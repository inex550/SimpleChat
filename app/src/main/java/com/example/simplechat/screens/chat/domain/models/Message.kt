package com.example.simplechat.screens.chat.domain.models

import com.google.gson.annotations.SerializedName

data class Message(
    val id: Int,
    val text: String,

    @SerializedName("sender_id")
    val senderId: Int,

    @SerializedName("chat_id")
    val chatId: Int,
)
