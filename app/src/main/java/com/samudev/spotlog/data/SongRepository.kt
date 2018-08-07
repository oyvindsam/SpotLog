package com.samudev.spotlog.data

import com.samudev.spotlog.utilities.runOnIoThread


class SongRepository(private val songDao: SongDao) {

    fun getSongsLatest(fromTime: Long) = songDao.getLatest(fromTime)

    fun getSongsAll() = songDao.getAll()

    fun logSong(song: Song) {
        runOnIoThread {
            songDao.insertSong(song)
        }
    }

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: SongRepository? = null

        fun getInstance(songDao: SongDao) =
                instance ?: synchronized(this) {
                    instance ?: SongRepository(songDao).also { instance = it }
                }
    }
}


fun SongDao.insertConditionally(song: Song, songs: List<Song>, time: Long): Boolean {
    if (songs.any { s -> s.trackId == song.trackId && song.registeredTime - s.registeredTime < time }) return false
    insertSong(song)
    return true
}
