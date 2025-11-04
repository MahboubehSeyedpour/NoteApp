package com.example.noteapp.data.repository

import com.example.noteapp.data.local.dao.TagDao
import com.example.noteapp.data.local.entity.TagEntity
import com.example.noteapp.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TagRepositoryImpl @Inject constructor(
    private val tagDao: TagDao
) : TagRepository {
    override fun getAllTags(): Flow<List<TagEntity>> = tagDao.getAllTags()

    override suspend fun addTag(tagEntity: TagEntity) = tagDao.addTag(tagEntity)

    override suspend fun deleteTag(tagEntity: TagEntity) = tagDao.deleteTag(tagEntity)

    override fun getTagById(id: Long): Flow<TagEntity> = tagDao.getTagByName(id)
}