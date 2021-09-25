package com.example.simplechat.core.coreimpl.common.di

import android.content.Context
import android.content.SharedPreferences
import com.example.simplechat.R
import com.example.simplechat.core.coreapi.common.preference.UserPreferenceStorage
import com.example.simplechat.core.coreimpl.common.preference.UserPreferenceStorageImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PreferenceModule {

    @Binds
    @Singleton
    abstract fun provideUserPreferenceStorage(impl: UserPreferenceStorageImpl): UserPreferenceStorage

    companion object {

        @Provides
        @Singleton
        fun providePrefs(
            @ApplicationContext context: Context
        ): SharedPreferences =
            context.getSharedPreferences(
                context.getString(R.string.pref_filename),
                Context.MODE_PRIVATE
            )
    }
}