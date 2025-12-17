package com.app.noteapp.data.repository

import android.util.Log
import androidx.room.withTransaction
import com.app.noteapp.data.local.dao.NoteDao
import com.app.noteapp.data.local.dao.TagDao
import com.app.noteapp.data.local.database.AppDatabase
import com.app.noteapp.data.local.entity.NoteEntity
import com.app.noteapp.data.local.entity.TagEntity
import com.app.noteapp.data.mapper.toNoteEntity
import com.app.noteapp.data.mapper.toTagEntity
import com.app.noteapp.domain.backup_model.ImportResult
import com.app.noteapp.domain.backup_model.NoteBackupDto
import com.app.noteapp.domain.backup_model.NotesBackupDto
import com.app.noteapp.domain.backup_model.TagBackupDto
import com.app.noteapp.domain.common_model.AppVersion
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
    private val noteDao: NoteDao,
    private val tagDao: TagDao,
    private val noteRepository: NoteRepository,
    private val tagRepository: TagRepository,
    private val db: AppDatabase,
    private val json: Json,
    private val appVersion: AppVersion
) : NotesBackupRepository {

    override suspend fun exportBackupJsonBytes(): ByteArray = withContext(Dispatchers.IO) {
        val notes = noteRepository.getNotesStream().first()
        val tags = tagRepository.getAllTags().first()

        val dto = NotesBackupDto(
            schemaVersion = 1,
            exportedAtEpochMs = System.currentTimeMillis(),
            appVersion = appVersion.value,
            tags = tags.map { t ->
                TagBackupDto(
                    id = t.id,
                    name = t.name,
                    colorArgb = t.color,
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
            })

        json.encodeToString(dto).encodeToByteArray()
    }

    override suspend fun importBackupJsonBytes(bytes: ByteArray): ImportResult =
        withContext(Dispatchers.IO) {
            runCatching {

                require(bytes.size <= 20 * 1024 * 1024) { "Backup file too large" }

                val imported = json.decodeFromString<NotesBackupDto>(bytes.decodeToString())

                require(imported.schemaVersion == 1) { "Unsupported schemaVersion: ${imported.schemaVersion}" }

                db.withTransaction {

                    val tagIdMap = mutableMapOf<Long, Long>()
                    var tagsImported = 0
                    var notesImported = 0

                    for (tag in imported.tags) {
                        val tagEntity = tag.toTagEntity()
                        val newId = insertTagWithIdConflictResolution(tagEntity)
                        if(newId == -1L) { // tag exists
                            tagIdMap[tag.id]= tagDao.getTagStream(tagEntity.name).first().id
                        } else {
                            tagIdMap[tag.id] = newId
                        }
                        tagsImported++
                    }

                    for (note in imported.notes) {
                        val newTagId = note.tagId?.let { old -> tagIdMap[old] ?: old }
                        val noteEntity = note.toNoteEntity().copy(tagId = newTagId)
                        insertNoteWithIdConflictResolution(noteEntity)
                        notesImported++
                    }

                    ImportResult(
                        tagsImported = tagsImported, notesImported = notesImported
                    )
                }
            }.getOrElse { e ->
                Log.e("Import", "IMPORT FAILED -> TX ROLLED BACK", e)
                throw e
            }
        }

    private suspend fun insertTagWithIdConflictResolution(tag: TagEntity): Long = tagDao.addTag(tag)

    private suspend fun insertNoteWithIdConflictResolution(note: NoteEntity): Long =
        noteDao.addNote(note)

}