package com.example.simplechat.screens.profille.data.di

import com.example.simplechat.screens.profille.data.repository.ProfileRepositoryImpl
import com.example.simplechat.screens.profille.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class ProfileModule {

    @Binds
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository
}