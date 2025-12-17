package com.app.noteapp.data.repository

import com.app.noteapp.data.local.dao.TagDao
import com.app.noteapp.domain.common_model.Tag
import com.app.noteapp.domain.mapper.toTag
import com.app.noteapp.domain.mapper.toTagEntity
import com.app.noteapp.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TagRepositoryImpl @Inject constructor(
    private val tagDao: TagDao
) : TagRepository {
    override fun getAllTags(): Flow<List<Tag>> = tagDao.getTagsStream().map { list -> list.map { it.toTag() } }

    override suspend fun addTag(tag: Tag): Long= tagDao.addTag(tag.toTagEntity())

    override suspend fun deleteTag(tag: Tag) = tagDao.deleteTag(tag.toTagEntity())

    override fun getTagById(id: Long): Flow<Tag> = tagDao.getTagStream(id).map { it.toTag() }
}