package com.example.simplechat.screens.searchuser.data.repository

import com.example.simplechat.core.network.apiservice.ApiService
import com.example.simplechat.screens.chats.domain.models.User
import com.example.simplechat.screens.searchuser.domain.repository.SearchUserRepository
import javax.inject.Inject

class SearchUserRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): SearchUserRepository {

    override suspend fun searchUsers(query: String): List<User> =
        apiService.searchUsers(query)
}