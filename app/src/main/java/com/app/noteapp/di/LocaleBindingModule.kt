package com.app.noteapp.di

import com.app.noteapp.data.repository.DataStoreLocaleRepository
import com.app.noteapp.domain.repository.LocaleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocaleBindingModule {

    @Binds
    @Singleton
    abstract fun bindLocaleRepository(
        impl: DataStoreLocaleRepository
    ): LocaleRepository
}
