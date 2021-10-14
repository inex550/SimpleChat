package com.example.simplechat.screens.profille.data.repository

import com.example.simplechat.core.coreimpl.network.apiservice.ApiService
import com.example.simplechat.screens.profille.domain.repository.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): ProfileRepository {

    override suspend fun changeUsername(username: String) {
        apiService.changeUsername(username)
    }
}