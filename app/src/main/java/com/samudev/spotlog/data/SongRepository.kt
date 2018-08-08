package com.samudev.spotlog.data

import android.arch.lifecycle.LiveData
import com.samudev.spotlog.history.LogTimeFilter
import com.samudev.spotlog.utilities.runOnIoThread


class SongRepository(private val songDao: SongDao) {

    // Keep a local cache of songs
    private lateinit var songs: LiveData<List<Song>>

    fun getSongsLatest(fromTime: Long): LiveData<List<Song>> {
        songs = songDao.getLatest(System.currentTimeMillis() - fromTime)
        return songs
    }

    fun getSongsAll(): LiveData<List<Song>> {
        songs = songDao.getAll()
        return songs
    }

    // log song if it is not recently logged
    fun logSong(song: Song) {
        runOnIoThread {
            songDao.insertConditionally(song, songs.value, LogTimeFilter.FIFTEEN_MINUTES)
        }
    }

    // save song
    fun saveSong(song: Song) {
        runOnIoThread {
            songDao.insertSong(song)
        }
    }

    fun removeSong(song: Song) {
        runOnIoThread {
            songDao.deleteSong(song)
        }
    }

    fun clearSongs() = songDao.clearTable()

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: SongRepository? = null

        fun getInstance(songDao: SongDao) =
                instance ?: synchronized(this) {
                    instance ?: SongRepository(songDao).also { instance = it }
                }
    }
}

fun SongDao.insertConditionally(song: Song, songs: List<Song>?, time: Long): Boolean {
    if (songs != null && songs.any { s -> s.trackId == song.trackId && song.registeredTime - s.registeredTime < time }) return false
    insertSong(song)
    return true
}
