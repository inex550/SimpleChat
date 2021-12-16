package com.example.simplechat.core.coreui.navigation.di

import com.example.simplechat.core.coreui.navigation.subnavigation.CiceroneHolder
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NavigationModule {

    private val cicerone: Cicerone<Router> = Cicerone.create(Router())

    @Provides
    @Singleton
    fun provideRouter(): Router = cicerone.router

    @Provides
    @Singleton
    fun provideNavigatorHolder(): NavigatorHolder = cicerone.getNavigatorHolder()

    @Provides
    @Singleton
    fun provideCiceroneHolder(): CiceroneHolder = CiceroneHolder()
}