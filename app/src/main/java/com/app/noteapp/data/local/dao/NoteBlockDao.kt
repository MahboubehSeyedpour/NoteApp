package com.app.noteapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.noteapp.data.local.entity.NoteBlockEntity
import com.app.noteapp.data.local.entity.NoteBlockMediaEntity
import com.app.noteapp.data.local.entity.NoteBlockTextEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteBlockDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertBlock(block: NoteBlockEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertText(text: NoteBlockTextEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMedia(media: NoteBlockMediaEntity)

    @Update
    suspend fun updateBlock(block: NoteBlockEntity)

    @Update
    suspend fun updateText(text: NoteBlockTextEntity)

    @Update
    suspend fun updateMedia(media: NoteBlockMediaEntity)

    @Query("SELECT * FROM note_blocks WHERE note_id=:noteId AND deleted_at IS NULL ORDER BY position ASC")
    fun getBlocksStream(noteId: Long): Flow<List<NoteBlockEntity>>

    @Query("DELETE FROM note_blocks WHERE note_id=:noteId")
    suspend fun hardDeleteByNoteId(noteId: Long)
}