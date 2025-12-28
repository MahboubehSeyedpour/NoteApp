package com.app.noteapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.noteapp.data.local.entity.UserEntity

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity): Long

    @Query("SELECT * FROM users ORDER BY id ASC LIMIT 1")
    suspend fun getDefaultUser(): UserEntity?

    @Query("SELECT id FROM users ORDER BY id ASC LIMIT 1")
    suspend fun getDefaultUserId(): Long?
}