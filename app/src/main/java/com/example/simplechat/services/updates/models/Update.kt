package com.example.simplechat.services.updates.models

import com.example.simplechat.screens.chat.domain.models.Message

data class Update(
    val message: Message?,
    val type: UpdateType
)