package com.samudev.spotlog.utilities

import android.content.Context
import com.samudev.spotlog.data.AppDatabase
import com.samudev.spotlog.data.SongRepository
import com.samudev.spotlog.viewmodels.SongLogViewModelFactory


object InjectorUtils {

    private fun getSongRepository(context: Context): SongRepository {
        return SongRepository.getInstance(AppDatabase.getInstance(context).songDao())
    }

    fun provideSongLogViewModelFactory(context: Context): SongLogViewModelFactory {
        val repository = getSongRepository(context)
        return SongLogViewModelFactory(repository)
    }

    fun provideSongRepository(context: Context): SongRepository {
        return getSongRepository(context)
    }
}
