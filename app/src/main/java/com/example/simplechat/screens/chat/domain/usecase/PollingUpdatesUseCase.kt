package com.example.simplechat.screens.chat.domain.usecase

import com.example.simplechat.screens.chat.domain.repository.ChatRepository
import com.example.simplechat.services.updates.models.UpdateNet
import javax.inject.Inject

class PollingUpdatesUseCase @Inject constructor(
    private val repository: ChatRepository
) {

    suspend fun invoke(): List<UpdateNet> = repository.pollingUpdates()
}