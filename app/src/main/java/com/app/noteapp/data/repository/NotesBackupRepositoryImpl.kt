package com.app.noteapp.data.repository

import com.app.noteapp.data.local.dao.NoteDao
import com.app.noteapp.data.local.dao.TagDao
import com.app.noteapp.data.local.database.AppDatabase
import com.app.noteapp.domain.model.common_model.AppVersion
import com.app.noteapp.domain.repository.NoteRepository
import com.app.noteapp.domain.repository.NotesBackupRepository
import com.app.noteapp.domain.repository.TagRepository
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

//    override suspend fun exportBackupJsonBytes(): ByteArray = withContext(Dispatchers.IO) {
//        val notes = noteRepository.getNotesStream().first()
//        val tags = tagRepository.getAllTags().first()
//
//        val dto = NotesBackupDto(
//            schemaVersion = 1,
//            exportedAtEpochMs = System.currentTimeMillis(),
//            appVersion = appVersion.value,
//            tags = tags.map { t ->
//                TagBackupDto(
//                    id = t.id,
//                    name = t.name,
//                    colorArgb = t.color,
//                )
//            },
//            notes = notes.map { n ->
//                NoteBackupDto(
//                    id = n.id,
//                    title = n.title,
//                    description = n.description,
//                    createdAtEpochMs = n.createdAt,
//                    pinned = n.pinned,
//                    tagId = n.tagId,
//                    reminderAtEpochMs = n.reminderAt,
//                )
//            })
//
//        json.encodeToString(dto).encodeToByteArray()
//    }
//
//    override suspend fun importBackupJsonBytes(bytes: ByteArray): ImportResult =
//        withContext(Dispatchers.IO) {
//            runCatching {
//
//                require(bytes.size <= 20 * 1024 * 1024) { "Backup file too large" }
//
//                val imported = json.decodeFromString<NotesBackupDto>(bytes.decodeToString())
//
//                require(imported.schemaVersion == 1) { "Unsupported schemaVersion: ${imported.schemaVersion}" }
//
//                db.withTransaction {
//
//                    val tagIdMap = mutableMapOf<Long, Long>()
//                    var tagsImported = 0
//                    var notesImported = 0
//
//                    for (tag in imported.tags) {
//                        val tagEntity = tag.toTagEntity()
//
//                        // check name collision (no DB constraint, so we do it manually)
//                        // TODO: Add DB constraints
//                        val nameExists = tagDao.getTagByName(tagEntity.name) != null
//                        val finalName = if (nameExists) resolveImportedTagName(tagEntity.name) else tagEntity.name
//
//                        // check id collision: if id exists, force new id
//                        val idExists = tagDao.getTagById(tagEntity.id) != null
//
//                        val toInsert = tagEntity.copy(
//                            id = if (idExists) 0 else tagEntity.id,
//                            name = finalName
//                        )
//
//                        val newId = tagDao.addTag(toInsert) // must be normal insert; returns new rowId
//                        if (newId <= 0) error("Failed to insert imported tag: ${toInsert.name}")
//
//                        tagIdMap[tagEntity.id] = newId
//                        tagsImported++
//                    }
//
//                    for (note in imported.notes) {
//                        val newTagId = note.tagId?.let { old -> tagIdMap[old] ?: old }
//                        val noteEntity = note.toNoteEntity().copy(tagId = newTagId)
//
//                        val existing = noteDao.getNoteById(noteEntity.id)
//
//                        val finalNote =
//                            if (existing == null) {
//                                noteEntity
//                            } else {
//                                noteEntity.copy(id = 0)
//                            }
//
//                        val newNoteId = noteDao.addNote(finalNote)
//
//                        if (newNoteId <= 0) error("Failed to insert imported note: title=${finalNote.title}")
//
//                        notesImported++
//
////                        insertNoteWithIdConflictResolution(noteEntity)
////                        notesImported++
//                    }
//
//                    ImportResult(
//                        tagsImported = tagsImported, notesImported = notesImported
//                    )
//                }
//            }.getOrElse { e ->
//                Log.e("Import", "IMPORT FAILED -> TX ROLLED BACK", e)
//                throw e
//            }
//        }
//
//    private suspend fun resolveImportedTagName(base: String): String {
//        val trimmed = base.trim()
//        if (trimmed.isEmpty()) return "Imported Tag"
//
//        // 1) Try "X (imported)"
//        val first = "$trimmed (imported)"
//        if (tagDao.getTagByName(first) == null) return first
//
//        // 2) Try "X (imported 2..N)"
//        var i = 2
//        while (true) {
//            val candidate = "$trimmed (imported $i)"
//            if (tagDao.getTagByName(candidate) == null) return candidate
//            i++
//        }
//    }
//
//
//    private suspend fun insertTag(tag: TagEntity): Long = tagDao.addTag(tag)
//
//    private suspend fun insertNoteWithIdConflictResolution(note: NoteEntity): Long =
//        noteDao.addNote(note)

}