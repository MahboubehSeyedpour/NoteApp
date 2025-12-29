package com.app.noteapp.domain.repository

import com.app.noteapp.domain.model.common_model.Tag
import kotlinx.coroutines.flow.Flow

interface TagRepository {

    fun getTagsStream(): Flow<List<Tag>>

    fun getTagStream(id: Long): Flow<Tag?>

    fun observeUsageCount(tagId: Long): Flow<Int>

    suspend fun addTag(tag: Tag): Long

    suspend fun updateTag(tag: Tag)

    suspend fun deleteTag(tag: Tag)

    suspend fun findTagByName(userId: Long, name: String): Tag?

    suspend fun attachToNote(noteId: Long, tagId: Long)

    suspend fun detachFromNote(noteId: Long, tagId: Long)

    suspend fun detachAllForNote(noteId: Long)
}