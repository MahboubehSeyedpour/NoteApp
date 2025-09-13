package com.example.noteapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.noteapp.core.constants.DatabaseConst
import com.example.noteapp.data.local.note.NoteDao
import com.example.noteapp.data.local.note.NoteEntity

@Database(
    entities = [NoteEntity::class],
    version = DatabaseConst.NOTES_DATABASE_VERSION,
    exportSchema = false
)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao
}