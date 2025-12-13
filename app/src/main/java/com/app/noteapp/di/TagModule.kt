package com.app.noteapp.di

import android.content.Context
import com.app.noteapp.data.local.dao.TagDao
import com.app.noteapp.data.repository.TagRepositoryImpl
import com.app.noteapp.domain.repository.TagRepository
import com.app.noteapp.domain.usecase.TagUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher

//@Module
//@InstallIn(ViewModelComponent::class)
//object TagModule {
//
//    @Provides
//    fun provideNoteRepository(tagDao: TagDao): TagRepository = TagRepositoryImpl(tagDao)
//
//    @Provides
//    fun provideTagUseCase(
//        tagRepository: TagRepository,
//        @ApplicationContext context: Context,
//        @IoDispatcher io: CoroutineDispatcher
//    ): TagUseCase = TagUseCase(
//        tagRepository = tagRepository, context = context, io = io
//    )
//}
