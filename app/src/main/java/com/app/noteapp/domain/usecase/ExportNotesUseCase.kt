package com.app.noteapp.domain.usecase

import com.app.noteapp.domain.repository.NotesBackupRepository
import javax.inject.Inject

class ExportNotesUseCase @Inject constructor(
    private val backupRepository: NotesBackupRepository
) {
    suspend operator fun invoke(): ByteArray = backupRepository.exportBackupJsonBytes()
}