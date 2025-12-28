package com.app.noteapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.noteapp.data.local.entity.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTag(tag: TagEntity): Long

    @Update
    suspend fun updateTag(tag: TagEntity)

    @Delete
    suspend fun deleteTag(tag: TagEntity)

    @Query("SELECT * FROM tags WHERE deleted_at IS NULL ORDER BY updated_at DESC")
    fun getTagsStream(): Flow<List<TagEntity>>

    @Query("SELECT * FROM tags WHERE id=:id LIMIT 1")
    fun getTagStream(id: Long): Flow<TagEntity?>

    @Query("SELECT * FROM tags WHERE user_id=:userId AND name=:name LIMIT 1")
    suspend fun getTagByName(userId: Long, name: String): TagEntity?

    @Query("""
        SELECT COUNT(*) 
        FROM note_tags nt
        JOIN notes n ON n.id = nt.note_id
        WHERE nt.tag_id = :tagId AND n.deleted_at IS NULL
    """)
    fun observeUsageCount(tagId: Long): Flow<Int>
}
