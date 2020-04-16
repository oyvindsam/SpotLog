package com.developments.samu.spotlog.data

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration

@Database(entities = [Song::class] , version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun songDao(): SongDao

    companion object {

        const val SONG_DB = "song"

        fun MIGRATION_1_2(): Migration {
            return object: Migration(1, 2) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("DROP TABLE IF EXISTS `$SONG_DB`")
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
    }
}

