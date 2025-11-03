package com.example.noteapp.domain.repository

import com.example.noteapp.data.local.tag.TagEntity
import kotlinx.coroutines.flow.Flow

interface TagRepository {
    fun getAllTags(): Flow<List<TagEntity>>
    suspend fun addTag(tagEntity: TagEntity)
    suspend fun deleteTag(tagEntity: TagEntity)
    fun getTagById(id: Long): Flow<TagEntity>
}