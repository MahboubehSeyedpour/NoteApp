package com.app.noteapp.data.repository

import com.app.noteapp.domain.backup_model.NoteBackupDto
import com.app.noteapp.domain.backup_model.NotesBackupDto
import com.app.noteapp.domain.backup_model.TagBackupDto
import com.app.noteapp.domain.repository.NoteRepository
import com.app.noteapp.domain.repository.NotesBackupRepository
import com.app.noteapp.domain.repository.TagRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesBackupRepositoryImpl @Inject constructor(
    private val noteRepository: NoteRepository,
    private val tagRepository: TagRepository,
    private val json: Json,
) : NotesBackupRepository {

    override suspend fun exportBackupJsonBytes(): ByteArray = withContext(Dispatchers.IO) {
        val notes = noteRepository.getAllNotes().first()
        val tags = tagRepository.getAllTags().first()

        val dto = NotesBackupDto(
            schemaVersion = 1,
            exportedAtEpochMs = System.currentTimeMillis(),
            appVersion = "1",
            tags = tags.map { t ->
                TagBackupDto(
                    id = t.id,
                    name = t.name,
                    colorArgb = t.colorArgb.toLong(),
                )
            },
            notes = notes.map { n ->
                NoteBackupDto(
                    id = n.id,
                    title = n.title,
                    description = n.description,
                    createdAtEpochMs = n.createdAt,
                    pinned = n.pinned,
                    tagId = n.tagId,
                    reminderAtEpochMs = n.reminderAt,
                )
            }
        )

        json.encodeToString(dto).encodeToByteArray()
    }
}