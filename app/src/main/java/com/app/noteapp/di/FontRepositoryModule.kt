package com.app.noteapp.di

import com.app.noteapp.data.repository.FontRepositoryImpl
import com.app.noteapp.domain.repository.FontRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FontRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFontRepository(
        impl: FontRepositoryImpl
    ): FontRepository
}