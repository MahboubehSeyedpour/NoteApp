package com.app.noteapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.noteapp.data.local.entity.NoteTagEntity

@Dao
interface NoteTagDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun attach(noteTag: NoteTagEntity)

    @Query("DELETE FROM note_tags WHERE note_id = :noteId AND tag_id = :tagId")
    suspend fun detach(noteId: Long, tagId: Long)

    @Query("DELETE FROM note_tags WHERE note_id = :noteId")
    suspend fun detachAllForNote(noteId: Long)
}