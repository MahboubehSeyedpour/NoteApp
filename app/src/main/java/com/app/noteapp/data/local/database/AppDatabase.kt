package com.app.noteapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.noteapp.core.constants.DatabaseConst
import com.app.noteapp.data.local.converters.DbConverters
import com.app.noteapp.data.local.dao.DirectoryDao
import com.app.noteapp.data.local.dao.NoteBlockDao
import com.app.noteapp.data.local.dao.NoteDao
import com.app.noteapp.data.local.dao.NoteTagDao
import com.app.noteapp.data.local.dao.ReminderDao
import com.app.noteapp.data.local.dao.TagDao
import com.app.noteapp.data.local.dao.UserDao
import com.app.noteapp.data.local.entity.DirectoryEntity
import com.app.noteapp.data.local.entity.NoteBlockEntity
import com.app.noteapp.data.local.entity.NoteBlockMediaEntity
import com.app.noteapp.data.local.entity.NoteBlockTextEntity
import com.app.noteapp.data.local.entity.NoteEntity
import com.app.noteapp.data.local.entity.NoteTagEntity
import com.app.noteapp.data.local.entity.ReminderEntity
import com.app.noteapp.data.local.entity.TagEntity
import com.app.noteapp.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class, DirectoryEntity::class, TagEntity::class, NoteEntity::class, NoteTagEntity::class, ReminderEntity::class, NoteBlockEntity::class, NoteBlockTextEntity::class, NoteBlockMediaEntity::class],
    version = DatabaseConst.NOTES_DATABASE_VERSION,
    exportSchema = true
)
@TypeConverters(DbConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun directoryDao(): DirectoryDao
    abstract fun tagDao(): TagDao
    abstract fun noteDao(): NoteDao
    abstract fun noteTagDao(): NoteTagDao
    abstract fun noteBlockDao(): NoteBlockDao
    abstract fun reminderDao(): ReminderDao
}