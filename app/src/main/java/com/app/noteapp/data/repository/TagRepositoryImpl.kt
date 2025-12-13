package com.app.noteapp.data.repository

import com.app.noteapp.data.local.dao.TagDao
import com.app.noteapp.data.local.entity.TagEntity
import com.app.noteapp.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TagRepositoryImpl @Inject constructor(
    private val tagDao: TagDao
) : TagRepository {
    override fun getAllTags(): Flow<List<TagEntity>> = tagDao.getAllTags()

    override suspend fun addTag(tagEntity: TagEntity): Long= tagDao.addTag(tagEntity)

    override suspend fun deleteTag(tagEntity: TagEntity) = tagDao.deleteTag(tagEntity)

    override fun getTagById(id: Long): Flow<TagEntity> = tagDao.getTagById(id)
}