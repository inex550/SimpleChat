package com.example.simplechat.screens.profille.domain.repository

import com.example.simplechat.core.coreimpl.network.apiservice.ApiService
import javax.inject.Inject

interface ProfileRepository {

    suspend fun changeUsername(username: String)
}