package com.app.noteapp.domain.repository

import com.app.noteapp.domain.common_model.Tag
import kotlinx.coroutines.flow.Flow

interface TagRepository {
    fun getAllTags(): Flow<List<Tag>>
    suspend fun addTag(tag: Tag): Long
    suspend fun deleteTag(tag: Tag)
    fun getTagById(id: Long): Flow<Tag>
}