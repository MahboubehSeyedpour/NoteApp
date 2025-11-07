package com.app.noteapp.di

import android.content.Context
import com.app.noteapp.data.local.dao.NoteDao
import com.app.noteapp.data.repository.NoteRepositoryImpl
import com.app.noteapp.domain.repository.NoteRepository
import com.app.noteapp.domain.usecase.NoteUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(ViewModelComponent::class)
object NoteModule {

    @Provides
    fun provideNoteRepository(noteDao: NoteDao): NoteRepository = NoteRepositoryImpl(noteDao)

    @Provides
    fun provideNoteUseCase(
        noteRepository: NoteRepository,
        @ApplicationContext context: Context,
        @IoDispatcher io: CoroutineDispatcher
    ): NoteUseCase = NoteUseCase(
        noteRepository = noteRepository,
        context = context,
        io = io
    )
}
