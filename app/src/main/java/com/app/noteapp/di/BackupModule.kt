package com.app.noteapp.di

import com.app.noteapp.data.repository.NotesBackupRepositoryImpl
import com.app.noteapp.domain.repository.NotesBackupRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BackupBindModule {

    @Binds
    @Singleton
    abstract fun bindNotesBackupRepository(
        impl: NotesBackupRepositoryImpl
    ): NotesBackupRepository
}

@Module
@InstallIn(SingletonComponent::class)
object BackupProvideModule {

    @Provides
    @Singleton
    fun provideBackupJson(): Json = Json {
        prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = true
        explicitNulls = false
    }
}
