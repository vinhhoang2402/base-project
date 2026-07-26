package com.demo.projectbase.feature.home.data.source.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// v1: remote_keys had (movieId INTEGER PRIMARY KEY, nextPage INTEGER)
// v2: remote_keys changed to (label TEXT PRIMARY KEY, nextPage INTEGER)
//     movies table unchanged — data is preserved
val MIGRATION_1_2 =
    object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("DROP TABLE IF EXISTS remote_keys")
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS remote_keys (label TEXT NOT NULL PRIMARY KEY, nextPage INTEGER)",
            )
        }
    }
