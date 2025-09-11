package com.example.noteapp.data.local.database

import android.app.Application
import androidx.room.Room
import com.example.noteapp.constants.DatabaseConst
import com.example.noteapp.data.local.dao.NoteDao

class NoteDatabaseProvider(private val application: Application) {

    @Volatile
    private var database: NoteDatabase? = null

    @Synchronized
    fun instance(): NoteDatabase {
        return database ?: synchronized(this) {
            database ?: buildDatabase().also { database = it }
        }
    }

    private fun buildDatabase(): NoteDatabase {
        return Room.databaseBuilder(application.applicationContext,
            NoteDatabase::class.java,
            DatabaseConst.NOTES_DATABASE_NAME)
            .build()
    }

    @Synchronized
    fun close() {
        database?.close()
        database = null
    }

    fun noteDao(): NoteDao {
        return instance().noteDao()
    }
}