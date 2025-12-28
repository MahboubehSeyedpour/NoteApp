package com.app.noteapp.di

import android.content.Context
import androidx.room.Room
import com.app.noteapp.core.constants.DatabaseConst
import com.app.noteapp.data.local.dao.DirectoryDao
import com.app.noteapp.data.local.dao.NoteBlockDao
import com.app.noteapp.data.local.dao.NoteDao
import com.app.noteapp.data.local.dao.NoteTagDao
import com.app.noteapp.data.local.dao.ReminderDao
import com.app.noteapp.data.local.dao.TagDao
import com.app.noteapp.data.local.dao.UserDao
import com.app.noteapp.data.local.database.AppDatabase
import com.app.noteapp.data.local.migrations.MIGRATION_1_2
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
    ): AppDatabase = Room.databaseBuilder(
        context, AppDatabase::class.java, DatabaseConst.NOTES_DATABASE_NAME
    ).addMigrations(MIGRATION_1_2).build()

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
    @Provides
    fun provideDirectoryDao(db: AppDatabase): DirectoryDao = db.directoryDao()
    @Provides
    fun provideTagDao(db: AppDatabase): TagDao = db.tagDao()
    @Provides
    fun provideNoteDao(db: AppDatabase): NoteDao = db.noteDao()
    @Provides
    fun provideNoteTagDao(db: AppDatabase): NoteTagDao = db.noteTagDao()
    @Provides
    fun provideNoteBlockDao(db: AppDatabase): NoteBlockDao = db.noteBlockDao()
    @Provides
    fun provideReminderDao(db: AppDatabase): ReminderDao = db.reminderDao()
}