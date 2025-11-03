package com.example.noteapp.data.local.tag

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTag(tagEntity: TagEntity)

    @Query("SELECT * FROM `tags-table`")
    fun getAllTags(): Flow<List<TagEntity>>

    @Delete
    suspend fun deleteTag(tagEntity: TagEntity)

    @Query("SELECT * FROM `tags-table` WHERE id=:id")
    fun getTagByName(id: Long): Flow<TagEntity>
}