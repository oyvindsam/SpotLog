package com.samudev.spotlog.data.db

import com.samudev.spotlog.data.Song
import com.samudev.spotlog.history.HistoryTimeFilter

fun SongDao.insertConditionally(song: Song, songs: List<Song>, time: Long): Boolean {
    if (songs.any { s -> s.trackId == song.trackId && song.registeredTime - s.registeredTime < time }) return false
    insertSong(song)
    return true
}

class DbManager {
    companion object {

    }
}