package com.example.simplechat.screens.searchuser.domain.repository

import com.example.simplechat.screens.chats.domain.models.User

interface SearchUserRepository {

    suspend fun searchUsers(query: String): List<User>
}