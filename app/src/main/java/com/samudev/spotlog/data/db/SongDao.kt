package com.samudev.spotlog.data.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.samudev.spotlog.data.Song


@Dao
interface SongDao {

    @Query("SELECT * FROM song ORDER BY registered_time DESC")
    fun getAll(): LiveData<List<Song>>

    @Query("SELECT * FROM song WHERE registered_time > :fromTime ORDER BY registered_time DESC")
    fun getLatest(fromTime: Long): List<Song>

    @Query("DELETE FROM song")
    fun clearTable()

    @Insert
    fun insertAll(songs: List<Song>)

    @Insert
    fun insertSong(song: Song): Long

    @Delete
    fun deleteSong(song: Song): Int
}