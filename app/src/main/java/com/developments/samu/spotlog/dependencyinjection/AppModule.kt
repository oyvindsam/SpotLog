package com.developments.samu.spotlog.dependencyinjection

import androidx.room.Room
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.developments.samu.spotlog.data.AppDatabase
import com.developments.samu.spotlog.data.SongDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [ContextModule::class, ViewModelModule::class])
class AppModule {

    @Provides
    @Singleton
    fun provideSongDao(appDatabase: AppDatabase): SongDao = appDatabase.songDao()

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room
                .databaseBuilder(context, AppDatabase::class.java, "song-history-db")
                .addMigrations(AppDatabase.MIGRATION_1_2())
                .build()
    }

    @Provides
    @Singleton
    fun provideDefaultSharedPreferences(@ApplicationContext context: Context): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

}