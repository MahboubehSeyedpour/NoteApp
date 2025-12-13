package com.app.noteapp.domain.repository

import com.app.noteapp.domain.backup_model.ImportResult

interface NotesBackupRepository {
    suspend fun exportBackupJsonBytes(): ByteArray
    suspend fun importBackupJsonBytes(bytes: ByteArray): ImportResult
}