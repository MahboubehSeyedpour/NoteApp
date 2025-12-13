package com.app.noteapp.di

import com.app.noteapp.data.repository.NoteRepositoryImpl
import com.app.noteapp.data.repository.TagRepositoryImpl
import com.app.noteapp.domain.repository.NoteRepository
import com.app.noteapp.domain.repository.TagRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNoteRepository(
        impl: NoteRepositoryImpl
    ): NoteRepository

    @Binds
    @Singleton
    abstract fun bindTagRepository(
        impl: TagRepositoryImpl
    ): TagRepository
}