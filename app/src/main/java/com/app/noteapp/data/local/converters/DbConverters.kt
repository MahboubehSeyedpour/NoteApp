package com.app.noteapp.data.local.converters

import androidx.room.TypeConverter
import com.app.noteapp.data.local.model.enums.BlockType
import com.app.noteapp.data.local.model.enums.MediaKind

class DbConverters {

    @TypeConverter
    fun blockTypeToInt(value: BlockType): Int = value.id

    @TypeConverter
    fun intToBlockType(value: Int): BlockType =
        BlockType.entries.firstOrNull { it.id == value }
            ?: error("Unknown BlockType id=$value")

    @TypeConverter
    fun mediaKindToInt(value: MediaKind): Int = value.id

    @TypeConverter
    fun intToMediaKind(value: Int): MediaKind =
        MediaKind.entries.firstOrNull { it.id == value }
            ?: error("Unknown MediaKind id=$value")
}