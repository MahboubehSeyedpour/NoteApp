package com.example.noteapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.noteapp.constants.DatabaseConst
import com.example.noteapp.data.local.dao.NoteDao
import com.example.noteapp.domain.model.Note

@Database(
    entities = [Note::class],
    version = DatabaseConst.NOTES_DATABASE_VERSION,
    exportSchema = false
)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao
}