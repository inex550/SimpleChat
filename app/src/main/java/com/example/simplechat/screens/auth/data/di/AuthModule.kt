package com.example.simplechat.screens.auth.data.di

import com.example.simplechat.screens.auth.data.repository.AuthRepositoryImpl
import com.example.simplechat.screens.auth.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class AuthModule {

    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}