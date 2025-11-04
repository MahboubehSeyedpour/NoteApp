package com.example.noteapp.domain.usecase

import android.content.Context
import com.example.noteapp.data.local.tag.TagEntity
import com.example.noteapp.di.IoDispatcher
import com.example.noteapp.domain.repository.TagRepository
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class TagUseCase(
    private val tagRepository: TagRepository,
    @ApplicationContext private val context: Context,
    @IoDispatcher private val io: CoroutineDispatcher
) {

    fun getAllTags(): Flow<List<TagEntity>> = tagRepository.getAllTags()

    fun getTag(id: Long): Flow<TagEntity> = tagRepository.getTagById(id)

    suspend fun addTag(tagEntity: TagEntity) {
        tagRepository.addTag(tagEntity)
    }

    suspend fun deleteTagById(id: Long) {
        withContext(io) {
            val tagToDelete = tagRepository.getTagById(id).first()
            tagRepository.deleteTag(tagToDelete)
        }
    }
}