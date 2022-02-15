package com.example.simplechat.screens.profille.domain.repository

import com.example.simplechat.screens.profille.domain.models.UploadedFile

interface ProfileRepository {

    suspend fun changeUsername(username: String)

    suspend fun changeAvatar(imageFilename: String)

    suspend fun uploadImageByURI(avatarFilepath: String): UploadedFile
}