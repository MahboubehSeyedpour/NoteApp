package com.example.noteapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.noteapp.data.local.entity.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTag(tagEntity: TagEntity)

    @Query("SELECT * FROM `tags`")
    fun getAllTags(): Flow<List<TagEntity>>

    @Delete
    suspend fun deleteTag(tagEntity: TagEntity)

    @Query("SELECT * FROM `tags` WHERE id=:id")
    fun getTagByName(id: Long): Flow<TagEntity>
}