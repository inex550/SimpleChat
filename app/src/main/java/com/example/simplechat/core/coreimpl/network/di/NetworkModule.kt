package com.example.simplechat.core.coreimpl.network.di

import com.example.simplechat.BuildConfig
import com.example.simplechat.core.coreimpl.network.apiservice.ApiService
import com.example.simplechat.core.coreimpl.network.interceptor.AppInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class ApiOkHttpClient

@Qualifier
annotation class WebSocketOkHttpClient

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    @ApiOkHttpClient
    fun provideOkHttpClient(
        interceptor: AppInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder().apply {
            addInterceptor(interceptor)
            readTimeout(30, TimeUnit.SECONDS)
            if (BuildConfig.DEBUG) addInterceptor(loggingInterceptor)
        }.build()

    @Provides
    @WebSocketOkHttpClient
    fun provideWebSocketOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .build()

    @Provides
    @Singleton
    fun provideGson(): Gson =
        GsonBuilder()
            .create()

    @Provides
    @Singleton
    fun provideApiService(
        retrofit: Retrofit
    ): ApiService =
        retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideRetrofit(
        @ApiOkHttpClient
        client: OkHttpClient,
        gson: Gson
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()


    companion object {

        //Local Address
        const val BASE_ADDRESS  = "192.168.1.105:80"

        private const val BASE_URL = "http://$BASE_ADDRESS/"
    }
}