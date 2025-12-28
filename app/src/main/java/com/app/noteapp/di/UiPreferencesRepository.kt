package com.app.noteapp.di

import com.app.noteapp.data.repository.DataStoreUiPreferencesRepository
import com.app.noteapp.data.repository.UiPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UiPreferencesModule {

    @Binds
    @Singleton
    abstract fun bindUiPrefs(
        impl: DataStoreUiPreferencesRepository
    ): UiPreferencesRepository
}