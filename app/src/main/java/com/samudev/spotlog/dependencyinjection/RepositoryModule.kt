package com.samudev.spotlog.dependencyinjection

import android.content.Context
import com.samudev.spotlog.data.AppDatabase
import com.samudev.spotlog.data.SongDao
import com.samudev.spotlog.data.SongRepository
import dagger.Module
import dagger.Provides

@Module(includes = [ContextModule::class])
class RepositoryModule {

    @Provides
    @SpotLogScope
    fun provideSongRepository(songDao: SongDao): SongRepository {
        return SongRepository.getInstance(songDao)
    }

    @Provides
    @SpotLogScope
    fun provideSongDao(appDatabase: AppDatabase): SongDao = appDatabase.songDao()

    @Provides
    @SpotLogScope
    fun provideAppDatabase(context: Context): AppDatabase = AppDatabase.getInstance(context)

}