package com.app.noteapp.domain.repository

interface NotesBackupRepository {
    suspend fun exportBackupJsonBytes(): ByteArray
}