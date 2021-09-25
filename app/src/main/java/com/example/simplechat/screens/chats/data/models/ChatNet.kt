package com.example.simplechat.screens.chats.data.models

import com.example.simplechat.screens.chats.domain.models.User

data class ChatNet(
    val id: Int,
    val name: String?,
    val users: List<User>
)