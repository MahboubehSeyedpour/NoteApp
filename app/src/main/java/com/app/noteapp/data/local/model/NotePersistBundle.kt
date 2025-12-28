package com.app.noteapp.data.local.model

import com.app.noteapp.data.local.entity.NoteBlockEntity
import com.app.noteapp.data.local.entity.NoteBlockMediaEntity
import com.app.noteapp.data.local.entity.NoteBlockTextEntity
import com.app.noteapp.data.local.entity.NoteEntity
import com.app.noteapp.data.local.entity.NoteTagEntity
import com.app.noteapp.data.local.entity.ReminderEntity

data class NotePersistBundle(
    val note: NoteEntity,
    val blocks: List<NoteBlockEntity>,
    val textBlocks: List<NoteBlockTextEntity>,
    val mediaBlocks: List<NoteBlockMediaEntity>,
    val noteTags: List<NoteTagEntity>,
    val reminders: List<ReminderEntity>
)