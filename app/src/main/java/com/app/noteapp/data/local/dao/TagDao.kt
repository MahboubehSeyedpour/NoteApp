package com.app.noteapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.noteapp.data.local.entity.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTag(tagEntity: TagEntity): Long

    @Query("SELECT * FROM `tags`")
    fun getTagsStream(): Flow<List<TagEntity>>

    @Delete
    suspend fun deleteTag(tagEntity: TagEntity)

    @Query("SELECT * FROM `tags` WHERE id=:id")
    fun getTagStream(id: Long): Flow<TagEntity>

    @Query("SELECT * FROM `tags` WHERE name=:name")
    fun getTagStream(name: String): Flow<TagEntity>

    @Query("SELECT * FROM tags WHERE id = :id")
    suspend fun getTagById(id: Long): TagEntity?

    @Query("SELECT id FROM tags")
    suspend fun getAllIds(): List<Long>

    @Query("SELECT * FROM tags WHERE name = :name LIMIT 1")
    suspend fun getTagByName(name: String): TagEntity?
}