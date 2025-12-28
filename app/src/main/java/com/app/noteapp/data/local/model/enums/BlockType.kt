package com.app.noteapp.data.local.model.enums

enum class BlockType(val id: Int) {
    TEXT(1),
    MEDIA(2),
    // TODO: CHECKLIST(3), CODE(4), DRAWING(5), ...
}

enum class MediaKind(val id: Int) {
    IMAGE(1),
    VIDEO(2),
    AUDIO(3),
}