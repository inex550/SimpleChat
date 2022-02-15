package com.example.simplechat.screens.searchuser.data.di

import com.example.simplechat.screens.searchuser.data.repository.SearchUserRepositoryImpl
import com.example.simplechat.screens.searchuser.domain.repository.SearchUserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface SearchUserModule {

    @Binds
    fun provideSearchUserRepository(impl: SearchUserRepositoryImpl): SearchUserRepository
}