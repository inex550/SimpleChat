package com.example.simplechat.screens.auth.data.repository

import com.example.simplechat.core.coreimpl.network.apiservice.ApiService
import com.example.simplechat.screens.auth.domain.models.AuthModel
import com.example.simplechat.screens.auth.domain.models.UserIdentifiers
import com.example.simplechat.screens.auth.domain.repository.AuthRepository
import timber.log.Timber
import java.io.Closeable
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): AuthRepository {

    override suspend fun login(username: String, password: String): UserIdentifiers =
        apiService.login(AuthModel(username, password))

    override suspend fun register(username: String, password: String): UserIdentifiers =
        apiService.register(AuthModel(username, password))
}