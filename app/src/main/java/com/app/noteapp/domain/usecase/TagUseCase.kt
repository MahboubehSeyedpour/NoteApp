package com.app.noteapp.domain.usecase

import com.app.noteapp.di.IoDispatcher
import com.app.noteapp.domain.common_model.Tag
import com.app.noteapp.domain.repository.TagRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TagUseCase @Inject constructor(
    private val tagRepository: TagRepository,
    @IoDispatcher private val io: CoroutineDispatcher
) {

    fun getAllTags(): Flow<List<Tag>> = tagRepository.getTagsStream()

    fun getTag(id: Long): Flow<Tag?> = tagRepository.getTagStream(id)

    suspend fun addTag(tag: Tag): Long = withContext(io) {
        tagRepository.addTag(tag)
    }

    suspend fun deleteTagById(id: Long) = withContext(io) {
        val tagToDelete = tagRepository
            .getTagStream(id)
            .firstOrNull()
            ?: return@withContext

        tagRepository.deleteTag(tagToDelete)
    }
}
