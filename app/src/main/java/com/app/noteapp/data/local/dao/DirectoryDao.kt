package com.app.noteapp.data.local.dao

import androidx.room.*
import com.app.noteapp.data.local.entity.DirectoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DirectoryDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(dir: DirectoryEntity): Long

    @Update
    suspend fun update(dir: DirectoryEntity)

    @Delete
    suspend fun delete(dir: DirectoryEntity)

    @Query("SELECT * FROM directories WHERE user_id=:userId AND deleted_at IS NULL ORDER BY sort_order ASC, updated_at DESC")
    fun getAllStream(userId: Long): Flow<List<DirectoryEntity>>
}