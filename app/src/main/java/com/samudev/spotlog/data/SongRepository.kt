package com.samudev.spotlog.data

import android.arch.lifecycle.LiveData
import com.samudev.spotlog.log.LogTimeFilter
import com.samudev.spotlog.utilities.runOnIoThread
import javax.inject.Inject


private val LOG_TAG: String = SongRepository::class.java.simpleName

class SongRepository @Inject constructor(val songDao: SongDao) {


    fun getSongsLatest(fromTime: Long): LiveData<List<Song>> {
        return songDao.getLatest(System.currentTimeMillis() - fromTime)
    }

    fun getSongsAll(): LiveData<List<Song>> =  songDao.getAll()

    fun getLastLoggedSong(callback: ((Song?) -> Unit)) {
        runOnIoThread {
            val song = songDao.getLatestLoggedSong()
            callback(song)
        }
    }

    // log song if it is not recently logged
    fun logSong(song: Song, callback: ((Song) -> Unit)) {
        runOnIoThread {
            if (!songDao.getLatestNoLiveData(LogTimeFilter.FIFTEEN_MINUTES)
                            .any { s -> s.trackId == song.trackId }) {
                songDao.insertSong(song)
                callback(song)
            }
        }
    }

    // save song
    fun saveSong(song: Song) = runOnIoThread { songDao.insertSong(song) }

    fun removeSong(song: Song) = runOnIoThread { songDao.deleteSong(song) }

    fun clearSongs() = runOnIoThread { songDao.clearTable() }
}
