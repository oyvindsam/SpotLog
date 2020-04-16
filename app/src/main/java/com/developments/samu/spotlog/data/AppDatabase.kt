package com.developments.samu.spotlog.data

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration

@Database(entities = [Song::class] , version = 3, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun songDao(): SongDao

    companion object {

        fun MIGRATION_1_2(): Migration {
            return object: Migration(1, 2) {
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
        }

        fun MIGRATION_2_3(): Migration {
            return object: Migration(2, 3) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("ALTER TABLE `Song` ADD COLUMN `playback_position` STRING NOT NULL DEFAULT ``")
                }
            }
        }
    }
}

