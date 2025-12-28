package com.app.noteapp.data.local.migrations


import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {

    override fun migrate(db: SupportSQLiteDatabase) {
        val now = System.currentTimeMillis()

        db.beginTransaction()
        try {
            // 0) Rename old tables
            db.execSQL("ALTER TABLE notes RENAME TO notes_old")
            db.execSQL("ALTER TABLE tags  RENAME TO tags_old")

            // 1) USERS
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    email TEXT,
                    phone TEXT,
                    name TEXT,
                    created_at INTEGER NOT NULL,
                    updated_at INTEGER NOT NULL,
                    deleted_at INTEGER,
                    server_row_id TEXT,
                    version INTEGER NOT NULL,
                    dirty INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE UNIQUE INDEX IF NOT EXISTS index_users_email 
                ON users(email)
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE UNIQUE INDEX IF NOT EXISTS index_users_server_row_id 
                ON users(server_row_id)
                """.trimIndent()
            )

            // default local user
            db.execSQL(
                """
                INSERT INTO users (email, phone, name, created_at, updated_at, deleted_at, server_row_id, version, dirty)
                VALUES (NULL, NULL, NULL, $now, $now, NULL, NULL, 0, 1)
                """.trimIndent()
            )
            val userId = db.query("SELECT last_insert_rowid()").use { c ->
                c.moveToFirst()
                c.getLong(0)
            }

            // 2) DIRECTORIES
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS directories (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    user_id INTEGER NOT NULL,
                    parent_id INTEGER,
                    name TEXT NOT NULL,
                    sort_order INTEGER NOT NULL,
                    created_at INTEGER NOT NULL,
                    updated_at INTEGER NOT NULL,
                    deleted_at INTEGER,
                    server_row_id TEXT,
                    version INTEGER NOT NULL,
                    dirty INTEGER NOT NULL,
                    FOREIGN KEY(user_id) REFERENCES users(id) 
                        ON DELETE CASCADE ON UPDATE CASCADE,
                    FOREIGN KEY(parent_id) REFERENCES directories(id) 
                        ON DELETE SET NULL ON UPDATE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS index_directories_user_id 
                ON directories(user_id)
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS index_directories_parent_id 
                ON directories(parent_id)
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE UNIQUE INDEX IF NOT EXISTS index_directories_user_id_server_row_id 
                ON directories(user_id, server_row_id)
                """.trimIndent()
            )

            // 3) TAGS
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS tags (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    user_id INTEGER NOT NULL,
                    name TEXT NOT NULL,
                    color_argb INTEGER NOT NULL,
                    created_at INTEGER NOT NULL,
                    updated_at INTEGER NOT NULL,
                    deleted_at INTEGER,
                    server_row_id TEXT,
                    version INTEGER NOT NULL,
                    dirty INTEGER NOT NULL,
                    FOREIGN KEY(user_id) REFERENCES users(id) 
                        ON DELETE CASCADE ON UPDATE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS index_tags_user_id 
                ON tags(user_id)
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE UNIQUE INDEX IF NOT EXISTS index_tags_user_id_name 
                ON tags(user_id, name)
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE UNIQUE INDEX IF NOT EXISTS index_tags_user_id_server_row_id 
                ON tags(user_id, server_row_id)
                """.trimIndent()
            )

            // preserve old tags
            db.execSQL(
                """
                INSERT INTO tags (id, user_id, name, color_argb, created_at, updated_at, deleted_at, server_row_id, version, dirty)
                SELECT id, $userId, name, color_argb, $now, $now, NULL, NULL, 0, 1
                FROM tags_old
                """.trimIndent()
            )

            // 4) NOTES
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS notes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    user_id INTEGER NOT NULL,
                    directory_id INTEGER,
                    title TEXT NOT NULL,
                    pinned INTEGER NOT NULL,
                    created_at INTEGER NOT NULL,
                    updated_at INTEGER NOT NULL,
                    deleted_at INTEGER,
                    server_row_id TEXT,
                    version INTEGER NOT NULL,
                    dirty INTEGER NOT NULL,
                    FOREIGN KEY(user_id) REFERENCES users(id) 
                        ON DELETE CASCADE ON UPDATE CASCADE,
                    FOREIGN KEY(directory_id) REFERENCES directories(id) 
                        ON DELETE SET NULL ON UPDATE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS index_notes_user_id 
                ON notes(user_id)
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS index_notes_directory_id 
                ON notes(directory_id)
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS index_notes_pinned 
                ON notes(pinned)
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS index_notes_created_at 
                ON notes(created_at)
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS index_notes_updated_at 
                ON notes(updated_at)
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE UNIQUE INDEX IF NOT EXISTS index_notes_user_id_server_row_id 
                ON notes(user_id, server_row_id)
                """.trimIndent()
            )

            // move notes from old table
            db.execSQL(
                """
                INSERT INTO notes (
                    id, user_id, directory_id, title, pinned,
                    created_at, updated_at, deleted_at,
                    server_row_id, version, dirty
                )
                SELECT
                    id,
                    $userId,
                    NULL,
                    title,
                    COALESCE(pinned, 0),
                    CASE 
                        WHEN created_at IS NULL OR created_at = 0 THEN $now 
                        ELSE created_at 
                    END,
                    $now,
                    NULL,
                    NULL,
                    0,
                    1
                FROM notes_old
                """.trimIndent()
            )

            // 5) NOTE_BLOCKS
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS note_blocks (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    note_id INTEGER NOT NULL,
                    position INTEGER NOT NULL,
                    type INTEGER NOT NULL,
                    created_at INTEGER NOT NULL,
                    updated_at INTEGER NOT NULL,
                    deleted_at INTEGER,
                    server_row_id TEXT,
                    version INTEGER NOT NULL,
                    dirty INTEGER NOT NULL,
                    FOREIGN KEY(note_id) REFERENCES notes(id) 
                        ON DELETE CASCADE ON UPDATE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS index_note_blocks_note_id 
                ON note_blocks(note_id)
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE UNIQUE INDEX IF NOT EXISTS index_note_blocks_note_id_position 
                ON note_blocks(note_id, position)
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE UNIQUE INDEX IF NOT EXISTS index_note_blocks_note_id_server_row_id 
                ON note_blocks(note_id, server_row_id)
                """.trimIndent()
            )

            // 6) NOTE_BLOCK_TEXT
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS note_block_text (
                    block_id INTEGER PRIMARY KEY NOT NULL,
                    text TEXT NOT NULL,
                    FOREIGN KEY(block_id) REFERENCES note_blocks(id) 
                        ON DELETE CASCADE ON UPDATE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS index_note_block_text_block_id 
                ON note_block_text(block_id)
                """.trimIndent()
            )

            // 7) NOTE_BLOCK_MEDIA
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS note_block_media (
                    block_id INTEGER PRIMARY KEY NOT NULL,
                    kind INTEGER NOT NULL,
                    local_uri TEXT NOT NULL,
                    mime_type TEXT,
                    width_px INTEGER,
                    height_px INTEGER,
                    duration_ms INTEGER,
                    size_bytes INTEGER,
                    FOREIGN KEY(block_id) REFERENCES note_blocks(id) 
                        ON DELETE CASCADE ON UPDATE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS index_note_block_media_block_id 
                ON note_block_media(block_id)
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS index_note_block_media_kind 
                ON note_block_media(kind)
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS index_note_block_media_mime_type 
                ON note_block_media(mime_type)
                """.trimIndent()
            )

            // 8) migrate old description -> first text block
            db.execSQL(
                """
                INSERT INTO note_blocks (
                    id, note_id, position, type, 
                    created_at, updated_at, deleted_at,
                    server_row_id, version, dirty
                )
                SELECT
                    id,
                    id,
                    0,
                    1,
                    CASE 
                        WHEN created_at IS NULL OR created_at = 0 THEN $now 
                        ELSE created_at 
                    END,
                    $now,
                    NULL,
                    NULL,
                    0,
                    1
                FROM notes_old
                WHERE description IS NOT NULL AND TRIM(description) <> ''
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO note_block_text (block_id, text)
                SELECT id, description
                FROM notes_old
                WHERE description IS NOT NULL AND TRIM(description) <> ''
                """.trimIndent()
            )

            // 9) NOTE_TAGS
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS note_tags (
                    note_id INTEGER NOT NULL,
                    tag_id INTEGER NOT NULL,
                    created_at INTEGER NOT NULL,
                    PRIMARY KEY(note_id, tag_id),
                    FOREIGN KEY(note_id) REFERENCES notes(id) 
                        ON DELETE CASCADE ON UPDATE CASCADE,
                    FOREIGN KEY(tag_id) REFERENCES tags(id) 
                        ON DELETE CASCADE ON UPDATE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS index_note_tags_note_id 
                ON note_tags(note_id)
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS index_note_tags_tag_id 
                ON note_tags(tag_id)
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO note_tags (note_id, tag_id, created_at)
                SELECT id, tag_id, $now
                FROM notes_old
                WHERE tag_id IS NOT NULL
                """.trimIndent()
            )

            // 10) REMINDERS
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS reminders (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    note_id INTEGER NOT NULL,
                    trigger_at INTEGER NOT NULL,
                    is_enabled INTEGER NOT NULL,
                    created_at INTEGER NOT NULL,
                    updated_at INTEGER NOT NULL,
                    deleted_at INTEGER,
                    server_row_id TEXT,
                    version INTEGER NOT NULL,
                    dirty INTEGER NOT NULL,
                    FOREIGN KEY(note_id) REFERENCES notes(id) 
                        ON DELETE CASCADE ON UPDATE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS index_reminders_note_id 
                ON reminders(note_id)
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS index_reminders_trigger_at 
                ON reminders(trigger_at)
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE UNIQUE INDEX IF NOT EXISTS index_reminders_note_id_server_row_id 
                ON reminders(note_id, server_row_id)
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO reminders (
                    note_id, trigger_at, is_enabled,
                    created_at, updated_at, deleted_at,
                    server_row_id, version, dirty
                )
                SELECT 
                    id, reminder_at, 1,
                    $now, $now, NULL,
                    NULL, 0, 1
                FROM notes_old
                WHERE reminder_at IS NOT NULL
                """.trimIndent()
            )

            // 11) drop old tables
            db.execSQL("DROP TABLE IF EXISTS notes_old")
            db.execSQL("DROP TABLE IF EXISTS tags_old")

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
}
