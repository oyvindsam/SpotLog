package com.samudev.spotlog.data.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.migration.Migration
import android.content.Context
import com.samudev.spotlog.data.Song
import java.util.prefs.AbstractPreferences


@Database(entities = arrayOf(Song::class) , version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao

    companion object {

        private var INSTANCE: AppDatabase? = null

        fun getAppDatabase(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context, AppDatabase::class.java, "song-history-db")
                            .addMigrations(AppDatabase.MIGRATION_1_2())
                            .allowMainThreadQueries()  // TODO: async
                            .build()
                }
            }
            return INSTANCE
        }

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

