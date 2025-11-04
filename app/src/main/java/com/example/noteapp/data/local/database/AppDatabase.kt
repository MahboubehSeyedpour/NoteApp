package com.example.noteapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.noteapp.core.constants.DatabaseConst
import com.example.noteapp.data.local.dao.NoteDao
import com.example.noteapp.data.local.entity.NoteEntity
import com.example.noteapp.data.local.dao.TagDao
import com.example.noteapp.data.local.entity.TagEntity

@Database(
    entities = [NoteEntity::class, TagEntity::class],
    version = DatabaseConst.NOTES_DATABASE_VERSION,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao
    abstract fun tagDao(): TagDao
}