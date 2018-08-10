package com.samudev.spotlog.dependencyinjection

import android.arch.persistence.room.Room
import android.content.Context
import com.samudev.spotlog.data.AppDatabase
import com.samudev.spotlog.data.SongDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [ContextModule::class])
class RepositoryModule {

    @Provides
    @Singleton
    fun provideSongDao(appDatabase: AppDatabase): SongDao = appDatabase.songDao()

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room
                .databaseBuilder(context, AppDatabase::class.java, "song-history-db")
                .addMigrations(AppDatabase.MIGRATION_1_2())
                .allowMainThreadQueries()
                .build()
    }

}