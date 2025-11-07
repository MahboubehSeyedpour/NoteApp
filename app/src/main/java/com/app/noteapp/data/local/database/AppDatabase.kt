package com.app.noteapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.noteapp.core.constants.DatabaseConst
import com.app.noteapp.data.local.dao.NoteDao
import com.app.noteapp.data.local.entity.NoteEntity
import com.app.noteapp.data.local.dao.TagDao
import com.app.noteapp.data.local.entity.TagEntity

@Database(
    entities = [NoteEntity::class, TagEntity::class],
    version = DatabaseConst.NOTES_DATABASE_VERSION,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao
    abstract fun tagDao(): TagDao
}