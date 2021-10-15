package com.example.simplechat.screens.profille.domain.repository

import com.example.simplechat.core.coreimpl.network.apiservice.ApiService
import com.example.simplechat.screens.profille.domain.models.UploadedFile
import java.net.URI
import javax.inject.Inject

interface ProfileRepository {

    suspend fun changeUsername(username: String)

    suspend fun changeAvatar(imageFilename: String)

    suspend fun uploadImageByURI(avatarFilepath: String): UploadedFile
}