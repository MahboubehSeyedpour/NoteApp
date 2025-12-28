package com.app.noteapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val APP_PREFS_NAME = "app_prefs"
private val Context.appPrefsDataStore by preferencesDataStore(name = APP_PREFS_NAME)

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideAppPrefsDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.appPrefsDataStore
}