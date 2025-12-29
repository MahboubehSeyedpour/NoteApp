package com.app.noteapp.data.repository

import com.app.noteapp.data.local.dao.NoteTagDao
import com.app.noteapp.data.local.dao.TagDao
import com.app.noteapp.data.local.entity.NoteTagEntity
import com.app.noteapp.data.mapper.toEntity
import com.app.noteapp.domain.model.common_model.Tag
import com.app.noteapp.domain.mapper.toDomain
import com.app.noteapp.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TagRepositoryImpl @Inject constructor(
    private val tagDao: TagDao,
    private val noteTagDao: NoteTagDao
) : TagRepository {

    override fun getTagsStream(): Flow<List<Tag>> =
        tagDao.getTagsStream()
            .map { entities -> entities.map { it.toDomain() } }

    override fun getTagStream(id: Long): Flow<Tag?> =
        tagDao.getTagStream(id)
            .map { it?.toDomain() }

    override fun observeUsageCount(tagId: Long): Flow<Int> =
        tagDao.observeUsageCount(tagId)

    override suspend fun addTag(tag: Tag): Long {
        require(tag.name.isNotBlank()) { "Tag.name must not be blank" }
        return tagDao.addTag(tag.toEntity())
    }

    override suspend fun updateTag(tag: Tag) {
        require(tag.id > 0) { "Tag.id must be > 0 for update" }
        require(tag.name.isNotBlank()) { "Tag.name must not be blank" }
        tagDao.updateTag(tag.toEntity())
    }

    override suspend fun deleteTag(tag: Tag) {
        require(tag.id > 0) { "Tag.id must be > 0 for delete" }
        tagDao.deleteTag(tag.toEntity())
    }

    override suspend fun findTagByName(userId: Long, name: String): Tag? {
        if (name.isBlank()) return null
        return tagDao.getTagByName(userId, name.trim())?.toDomain()
    }

    override suspend fun attachToNote(noteId: Long, tagId: Long) {
        require(noteId > 0) { "noteId must be > 0" }
        require(tagId > 0) { "tagId must be > 0" }

        val now = System.currentTimeMillis()
        noteTagDao.attach(
            NoteTagEntity(
                noteId = noteId,
                tagId = tagId,
                createdAt = now
            )
        )
    }

    override suspend fun detachFromNote(noteId: Long, tagId: Long) {
        require(noteId > 0) { "noteId must be > 0" }
        require(tagId > 0) { "tagId must be > 0" }
        noteTagDao.detach(noteId, tagId)
    }

    override suspend fun detachAllForNote(noteId: Long) {
        require(noteId > 0) { "noteId must be > 0" }
        noteTagDao.detachAllForNote(noteId)
    }
}