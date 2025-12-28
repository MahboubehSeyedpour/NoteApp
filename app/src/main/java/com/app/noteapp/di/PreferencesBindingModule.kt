package com.app.noteapp.di


import com.app.noteapp.data.repository.AppPreferencesRepository
import com.app.noteapp.data.repository.DataStoreAppPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PreferencesBindingModule {

    @Binds
    @Singleton
    abstract fun bindAppPreferencesRepository(
        impl: DataStoreAppPreferencesRepository
    ): AppPreferencesRepository
}