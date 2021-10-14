package com.example.simplechat.screens.chats.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String,
    val username: String,
    val avatar: String?
): Parcelable
