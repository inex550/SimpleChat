package com.example.simplechat.screens.chats.data.models

import android.content.Context
import com.example.simplechat.R
import com.example.simplechat.screens.chats.domain.models.Chat
import com.example.simplechat.screens.chats.domain.models.User

data class ChatNet(
    val id: Int,
    val name: String?,
    val users: List<User>
) {

    fun transform(context: Context, currentUserId: String): Chat {
        return if (users.size == 2) {
            val user = users.firstOrNull { it.id != currentUserId }

            Chat(
                id = id,
                name = name ?: user?.username ?: context.getString(R.string.chats_no_name),
                avatar = user?.avatar,
                user = user
            )
        } else Chat(
            id = id,
            name = name ?: context.getString(R.string.chats_no_name),
            users = users
        )
    }
}