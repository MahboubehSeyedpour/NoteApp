package com.example.noteapp.di

import android.content.Context
import androidx.room.Room
import com.example.noteapp.core.constants.DatabaseConst
import com.example.noteapp.data.local.note.NoteDao
import com.example.noteapp.data.local.database.AppDatabase
import com.example.noteapp.data.local.tag.TagDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DatabaseConst.NOTES_DATABASE_NAME
        )
            .build()

    @Provides
    fun provideNoteDao(database: AppDatabase): NoteDao = database.noteDao()
    @Provides
    fun provideTagDao(database: AppDatabase): TagDao = database.tagDao()
}
