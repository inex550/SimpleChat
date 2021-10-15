package com.example.simplechat.screens.profille.data.repository

import com.example.simplechat.core.coreimpl.network.apiservice.ApiService
import com.example.simplechat.screens.profille.domain.models.UploadedFile
import com.example.simplechat.screens.profille.domain.repository.ProfileRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.net.URI
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): ProfileRepository {

    override suspend fun changeUsername(username: String) {
        apiService.changeUsername(username)
    }

    override suspend fun changeAvatar(imageFilename: String) {
        apiService.changeAvatar(imageFilename)
    }

    override suspend fun uploadImageByURI(avatarFilepath: String): UploadedFile {
        val file = File(avatarFilepath)
        val imageBody = MultipartBody.Part.createFormData(
            name = "image",
            filename = file.name,
            body = file.asRequestBody("image/*".toMediaTypeOrNull())
        )

        return apiService.uploadImage(imageBody)
    }
}