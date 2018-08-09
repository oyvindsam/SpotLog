package com.samudev.spotlog.data

import android.arch.lifecycle.LiveData
import android.util.Log
import com.samudev.spotlog.log.LogTimeFilter
import com.samudev.spotlog.utilities.runOnIoThread
import javax.inject.Inject


class SongRepository @Inject constructor(val songDao: SongDao) {

    private val LOG_TAG: String = SongRepository::class.java.simpleName

    // Keep a local cache of songs
    private var songs = songDao.getAll()

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
}

fun SongDao.insertConditionally(song: Song, songs: List<Song>?, time: Long): Boolean {
    if (songs != null && songs.any { s -> s.trackId == song.trackId && s.registeredTime - song.registeredTime < time }) return false
    Log.d("SongDao-", "New Song saved in db")
    insertSong(song)
    return true
}
