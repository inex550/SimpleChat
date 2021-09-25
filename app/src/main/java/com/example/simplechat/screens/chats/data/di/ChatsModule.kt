package com.example.simplechat.screens.chats.data.di

import com.example.simplechat.screens.chats.data.repository.ChatsRepositoryImpl
import com.example.simplechat.screens.chats.domain.repository.ChatsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
abstract class ChatsModule {

    @Binds
    abstract fun bindChatsRepository(impl: ChatsRepositoryImpl): ChatsRepository
}