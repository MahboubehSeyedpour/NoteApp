package com.app.noteapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.app.noteapp.data.local.entity.NoteBlockEntity
import com.app.noteapp.data.local.entity.NoteBlockMediaEntity
import com.app.noteapp.data.local.entity.NoteBlockTextEntity
import com.app.noteapp.data.local.entity.NoteEntity
import com.app.noteapp.data.local.entity.NoteTagEntity
import com.app.noteapp.data.local.relation.NoteWithRelations
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    // ---- CRUD ----
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addNote(note: NoteEntity): Long

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("SELECT id FROM notes ORDER BY id DESC LIMIT 1")
    suspend fun getLastNoteId(): Long?

    @Query("SELECT id FROM notes")
    suspend fun getAllIds(): List<Long>

    // ---- Blocks ----

    @Insert
    suspend fun insertBlock(block: NoteBlockEntity): Long

    @Insert
    suspend fun insertMediaDetail(media: NoteBlockMediaEntity)

    @Insert
    suspend fun insertTextDetail(text: NoteBlockTextEntity)

    @Query("""
        SELECT COALESCE(MAX(position), -1) + 1 
        FROM note_blocks 
        WHERE note_id = :noteId
    """)
    suspend fun getNextBlockPosition(noteId: Long): Int

    @Query("""
        UPDATE note_blocks
        SET position = position + 1
        WHERE note_id = :noteId
          AND deleted_at IS NULL
          AND position >= :fromPositionInclusive
    """)
    suspend fun shiftBlockPositionsRight(
        noteId: Long,
        fromPositionInclusive: Int
    )

    @Query("""
        UPDATE note_blocks
        SET deleted_at = :deletedAt,
            dirty      = 1,
            updated_at = :deletedAt
        WHERE id = :blockId
    """)
    suspend fun markBlockDeleted(
        blockId: Long,
        deletedAt: Long
    )

    @Query("""
        UPDATE note_blocks
        SET 
            position = CASE
                WHEN position = :fromPosition THEN :toPosition
                WHEN :fromPosition < :toPosition 
                     AND position > :fromPosition 
                     AND position <= :toPosition THEN position - 1
                WHEN :fromPosition > :toPosition
                     AND position >= :toPosition
                     AND position < :fromPosition THEN position + 1
                ELSE position
            END,
            updated_at = :updatedAt,
            dirty      = 1
        WHERE note_id = :noteId
          AND deleted_at IS NULL
    """)
    suspend fun moveBlock(
        noteId: Long,
        fromPosition: Int,
        toPosition: Int,
        updatedAt: Long
    )

    @Transaction
    suspend fun updateTextBlock(
        blockId: Long,
        newText: String,
        updatedAt: Long
    ) {
        updateTextDetailInternal(blockId, newText)
        updateBlockUpdatedAtInternal(blockId, updatedAt)
    }

    @Query("""
        UPDATE note_block_text
        SET text = :newText
        WHERE block_id = :blockId
    """)
    suspend fun updateTextDetailInternal(
        blockId: Long,
        newText: String
    )

    @Query("""
        UPDATE note_blocks
        SET updated_at = :updatedAt,
            dirty      = 1
        WHERE id = :blockId
    """)
    suspend fun updateBlockUpdatedAtInternal(
        blockId: Long,
        updatedAt: Long
    )

    // ---- Streams (with relations) ----

    @Transaction
    @Query("""
        SELECT * FROM notes
        WHERE deleted_at IS NULL
        ORDER BY pinned DESC, updated_at DESC
    """)
    fun getNotesStream(): Flow<List<NoteWithRelations>>

    @Transaction
    @Query("""
        SELECT * FROM notes
        WHERE id = :id AND deleted_at IS NULL
        LIMIT 1
    """)
    fun getNoteStream(id: Long): Flow<NoteWithRelations?>

    @Transaction
    @Query("""
        SELECT * FROM notes
        WHERE deleted_at IS NULL 
          AND created_at >= :start 
          AND created_at < :end
        ORDER BY created_at DESC
    """)
    fun getNotesBetweenStream(start: Long, end: Long): Flow<List<NoteWithRelations>>

    // ---- Tags junction ----

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun attachTag(join: NoteTagEntity)

    @Query("DELETE FROM note_tags WHERE note_id = :noteId AND tag_id = :tagId")
    suspend fun detachTag(noteId: Long, tagId: Long)

    @Query("DELETE FROM note_tags WHERE note_id = :noteId")
    suspend fun detachAllTags(noteId: Long)
}
