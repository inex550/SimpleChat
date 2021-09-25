package com.example.simplechat.core.coreimpl.common.di

import com.example.simplechat.core.coreapi.common.error.ErrorHandler
import com.example.simplechat.core.coreimpl.common.error.ErrorHandlerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CommonModule {

    @Binds
    @Singleton
    abstract fun bindErrorHandler(impl: ErrorHandlerImpl): ErrorHandler
}