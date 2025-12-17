package com.app.noteapp.di

import android.content.Context
import com.app.noteapp.domain.repository.NoteRepository
import com.app.noteapp.domain.repository.TagRepository
import com.app.noteapp.domain.usecase.NoteUseCase
import com.app.noteapp.domain.usecase.TagUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    fun provideNoteUseCase(
        noteRepository: NoteRepository,
        @ApplicationContext context: Context,
        @IoDispatcher io: CoroutineDispatcher
    ): NoteUseCase =
        NoteUseCase(noteRepository, io)

    @Provides
    fun provideTagUseCase(
        tagRepository: TagRepository,
        @ApplicationContext context: Context,
        @IoDispatcher io: CoroutineDispatcher
    ): TagUseCase =
        TagUseCase(tagRepository, io)
}
