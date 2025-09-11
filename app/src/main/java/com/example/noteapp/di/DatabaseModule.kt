package com.example.noteapp.di

import android.content.Context
import androidx.room.Room
import com.example.noteapp.constants.DatabaseConst
import com.example.noteapp.data.local.dao.NoteDao
import com.example.noteapp.data.local.database.NoteDatabase
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
    ): NoteDatabase =
        Room.databaseBuilder(
            context,
            NoteDatabase::class.java,
            DatabaseConst.NOTES_DATABASE_NAME
        )
            .build()

    @Provides
    fun provideNoteDao(db: NoteDatabase): NoteDao = db.noteDao()
}
