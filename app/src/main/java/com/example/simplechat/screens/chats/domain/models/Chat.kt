package com.example.simplechat.screens.chats.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Chat(
    val id: Int,
    val name: String,

    val user: User? = null,
    val users: List<User>? = null
): Parcelable
