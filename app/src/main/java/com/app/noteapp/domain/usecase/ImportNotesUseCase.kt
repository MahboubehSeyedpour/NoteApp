package com.app.noteapp.domain.usecase

import com.app.noteapp.domain.backup_model.ImportResult
import com.app.noteapp.domain.repository.NotesBackupRepository
import javax.inject.Inject

class ImportNotesUseCase @Inject constructor(
    private val backupRepository: NotesBackupRepository
) {
    suspend operator fun invoke(bytes: ByteArray): ImportResult =
        backupRepository.importBackupJsonBytes(bytes)
}
