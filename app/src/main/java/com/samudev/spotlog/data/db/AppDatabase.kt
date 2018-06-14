package com.samudev.spotlog.data.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.migration.Migration
import com.samudev.spotlog.data.Song


@Database(entities = arrayOf(Song::class) , version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao

    companion object {
        fun MIGRATION_1_2(): Migration {
            val migration: Migration = object: Migration(1, 2) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("DROP TABLE IF EXISTS `Song`")
                    database.execSQL("CREATE TABLE `Song` (" +
                            "`track_id` TEXT NOT NULL, " +
                            "`artist` TEXT NOT NULL, " +
                            "`album` TEXT NOT NULL, " +
                            "`track` TEXT NOT NULL, " +
                            "`track_length` INTEGER NOT NULL, " +
                            "`registered_time` INTEGER NOT NULL, " +
                            "PRIMARY KEY(`track_id`, `registered_time`))")
                }
            }
            return migration
        }
    }
}

