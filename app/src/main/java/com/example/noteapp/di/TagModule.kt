package com.example.noteapp.di

import android.content.Context
import com.example.noteapp.data.local.note.NoteDao
import com.example.noteapp.data.local.tag.TagDao
import com.example.noteapp.data.repository.NoteRepositoryImpl
import com.example.noteapp.data.repository.TagRepositoryImpl
import com.example.noteapp.domain.repository.NoteRepository
import com.example.noteapp.domain.repository.TagRepository
import com.example.noteapp.domain.usecase.NoteUseCase
import com.example.noteapp.domain.usecase.TagUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(ViewModelComponent::class)
object TagModule {

    @Provides
    fun provideNoteRepository(tagDao: TagDao): TagRepository = TagRepositoryImpl(tagDao)

    @Provides
    fun provideTagUseCase(
        tagRepository: TagRepository,
        @ApplicationContext context: Context,
        @IoDispatcher io: CoroutineDispatcher
    ): TagUseCase = TagUseCase(
        tagRepository = tagRepository,
        context = context,
        io = io
    )
}
