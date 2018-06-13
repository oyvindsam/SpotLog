package com.samudev.spotlog.data.db

import com.samudev.spotlog.data.Song

// Here is the db... or not.
object Database {

    fun getSongs(count: Int) : List<Song> {
        val songs: MutableList<Song> = mutableListOf<Song>()
        for (i in 1..count) {
            songs.add(Song(
                    "id=$i",
                    "Artist",
                    "Album",
                    "Track",
                    320)
            )
        }
        return songs
    }
}