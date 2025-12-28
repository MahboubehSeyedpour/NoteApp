package com.app.noteapp.data.repository

import com.app.noteapp.data.local.dao.NoteDao
import com.app.noteapp.data.local.entity.NoteBlockEntity
import com.app.noteapp.data.local.entity.NoteBlockMediaEntity
import com.app.noteapp.data.local.entity.NoteTagEntity
import com.app.noteapp.data.local.model.enums.BlockType
import com.app.noteapp.data.local.model.enums.MediaKind
import com.app.noteapp.data.mapper.toNoteEntity
import com.app.noteapp.domain.common_model.Note
import com.app.noteapp.domain.mapper.toDomain
import com.app.noteapp.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : NoteRepository {

    // ---- CRUD ----
    override suspend fun addNote(note: Note): Long {
        require(note.title.isNotBlank()) { "Note title must not be blank" }
        require(note.userId > 0) { "userId must be > 0" }

        return noteDao.addNote(note.toNoteEntity())
    }

    override suspend fun updateNote(note: Note) {
        require(note.id > 0) { "Cannot update note with id <= 0" }
        require(note.title.isNotBlank()) { "Note title must not be blank" }

        noteDao.updateNote(note.toNoteEntity())
    }

    override suspend fun deleteNote(note: Note) {
        require(note.id > 0) { "Cannot delete note with id <= 0" }
        noteDao.deleteNote(note.toNoteEntity())
    }

    override suspend fun getLastNoteId(): Long? =
        noteDao.getLastNoteId()

    override suspend fun getAllIds(): List<Long> =
        noteDao.getAllIds()

    // ---- Streams ----
    override fun getNotesStream(): Flow<List<Note>> =
        noteDao.getNotesStream()
            .map { list -> list.map { it.toDomain() } }

    override fun getNoteStream(id: Long): Flow<Note?> {
        require(id > 0) { "id must be > 0" }
        return noteDao.getNoteStream(id)
            .map { it?.toDomain() }
    }

    override fun getNotesBetweenStream(start: Long, end: Long): Flow<List<Note>> {
        require(start >= 0) { "start timestamp must be >= 0" }
        require(end >= start) { "end must be >= start" }

        return noteDao.getNotesBetweenStream(start, end)
            .map { list -> list.map { it.toDomain() } }
    }

    // ---- Tags junction ----
    override suspend fun attachTag(noteId: Long, tagId: Long, createdAt: Long) {
        require(noteId > 0) { "noteId must be > 0" }
        require(tagId > 0) { "tagId must be > 0" }
        require(createdAt > 0) { "createdAt must be > 0" }

        val join = NoteTagEntity(
            noteId = noteId,
            tagId = tagId,
            createdAt = createdAt
        )
        noteDao.attachTag(join)
    }

    override suspend fun detachTag(noteId: Long, tagId: Long) {
        require(noteId > 0) { "noteId must be > 0" }
        require(tagId > 0) { "tagId must be > 0" }

        noteDao.detachTag(noteId, tagId)
    }

    override suspend fun detachAllTags(noteId: Long) {
        require(noteId > 0) { "noteId must be > 0" }
        noteDao.detachAllTags(noteId)
    }

    override suspend fun appendMediaBlock(
        noteId: Long,
        kind: MediaKind,
        localUri: String
    ) {
        require(noteId > 0) { "noteId must be > 0" }
        require(localUri.isNotBlank()) { "localUri must not be blank" }

        val now = System.currentTimeMillis()

        val nextPosition = noteDao.getNextBlockPosition(noteId)

        val blockId = noteDao.insertBlock(
            NoteBlockEntity(
                id = 0,
                noteId = noteId,
                position = nextPosition,
                type = BlockType.MEDIA,
                createdAt = now,
                updatedAt = now,
                deletedAt = null,
                serverId = null,
                version = 0,
                dirty = true
            )
        )

        if (blockId <= 0) return

        noteDao.insertMediaDetail(
            NoteBlockMediaEntity(
                blockId = blockId,
                kind = kind,
                localUri = localUri,
                mimeType = null,
                widthPx = null,
                heightPx = null,
                durationMs = null,
                sizeBytes = null
            )
        )
    }
}