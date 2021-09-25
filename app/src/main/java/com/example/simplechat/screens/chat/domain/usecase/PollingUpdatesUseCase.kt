package com.example.simplechat.screens.chat.domain.usecase

import com.example.simplechat.screens.chat.domain.models.Update
import com.example.simplechat.screens.chat.domain.repository.ChatRepository
import javax.inject.Inject

class PollingUpdatesUseCase @Inject constructor(
    private val repository: ChatRepository
) {

    suspend fun invoke(): List<Update> = repository.pollingUpdates()
}