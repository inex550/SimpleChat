package com.example.simplechat.core.coreimpl.network.apiservice

import com.example.simplechat.screens.auth.domain.models.AuthModel
import com.example.simplechat.screens.auth.domain.models.UserIdentifiers
import com.example.simplechat.screens.chat.domain.models.Message
import com.example.simplechat.screens.chat.domain.models.SendMessage
import com.example.simplechat.screens.chat.domain.models.Update
import com.example.simplechat.screens.chats.data.models.ChatNet
import com.example.simplechat.screens.chats.data.models.NewPrivateChat
import com.example.simplechat.screens.chats.domain.models.Chat
import okhttp3.ResponseBody
import retrofit2.http.*

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body authModel: AuthModel): UserIdentifiers

    @POST("auth/register")
    suspend fun register(@Body authModel: AuthModel): UserIdentifiers

    @POST("chat/{chatId}/sendMessage")
    suspend fun sendMessage(@Path("chatId") chatId: Int, @Body message: SendMessage): Message

    @GET("getUpdates")
    suspend fun getUpdates(): List<Update>

    @GET("chat/my")
    suspend fun getMyChats(): List<ChatNet>

    @POST("chat/newPrivate")
    suspend fun createPrivateChat(@Body newPrivateChat: NewPrivateChat): ChatNet

    @GET("chat/{chatId}/messages")
    suspend fun getChatMessages(
        @Path("chatId") chatId: Int,
        @Query("start") start: Int? = null,
        @Query("batch") batch: Int? = null
    ): List<Message>

    @DELETE("chat/{chatId}")
    suspend fun deleteChat(@Path("chatId") chatId: Int): ResponseBody

    @POST("user/username/change")
    suspend fun changeUsername(@Query("username") username: String): ResponseBody
}