package com.app.noteapp.domain.usecase

import com.app.noteapp.di.IoDispatcher
import com.app.noteapp.domain.common_model.Tag
import com.app.noteapp.domain.repository.TagRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class TagUseCase(
    private val tagRepository: TagRepository, @IoDispatcher private val io: CoroutineDispatcher
) {

    fun getAllTags(): Flow<List<Tag>> = tagRepository.getAllTags()

    fun getTag(id: Long): Flow<Tag> = tagRepository.getTagById(id)

    suspend fun addTag(tag: Tag) { tagRepository.addTag(tag) }

    suspend fun deleteTagById(id: Long) {
        withContext(io) {
            val tagToDelete = tagRepository.getTagById(id).first()
            tagRepository.deleteTag(tagToDelete)
        }
    }
}