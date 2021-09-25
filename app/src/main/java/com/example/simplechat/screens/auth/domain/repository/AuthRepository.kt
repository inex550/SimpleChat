package com.example.simplechat.screens.auth.domain.repository

import com.example.simplechat.screens.auth.domain.models.UserIdentifiers

interface AuthRepository {

    suspend fun login(username: String, password: String): UserIdentifiers

    suspend fun register(username: String, password: String): UserIdentifiers
}