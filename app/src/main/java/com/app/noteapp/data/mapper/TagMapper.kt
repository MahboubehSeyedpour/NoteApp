package com.app.noteapp.data.mapper

import com.app.noteapp.data.local.entity.TagEntity
import com.app.noteapp.domain.backup_model.TagBackupDto

fun TagBackupDto.toTagEntity(): TagEntity = TagEntity(id = id, name = name, colorArgb = colorArgb)
