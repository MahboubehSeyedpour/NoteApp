package com.example.noteapp.di

import android.content.Context
import com.example.noteapp.data.local.note.NoteDao
import com.example.noteapp.data.repository.NoteRepositoryImpl
import com.example.noteapp.domain.repository.NoteRepository
import com.example.noteapp.domain.usecase.NoteUseCase
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
