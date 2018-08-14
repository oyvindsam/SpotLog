package com.samudev.spotlog.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*


@Dao
interface SongDao {

    @Query("SELECT * FROM song ORDER BY registered_time DESC")
    fun getAll(): LiveData<List<Song>>

    @Query("SELECT * FROM song WHERE registered_time > :fromTime ORDER BY registered_time DESC")
    fun getLatest(fromTime: Long): LiveData<List<Song>>

    @Query("SELECT * FROM song WHERE registered_time > :fromTime ORDER BY registered_time DESC")
    fun getLatestNoLiveData(fromTime: Long): List<Song>


    @Query("SELECT * FROM song ORDER BY registered_time DESC LIMIT 1")
    fun getLatestLoggedSong(): Song?

    @Query("DELETE FROM song")
    fun clearTable()

    @Insert
    fun insertAll(songs: List<Song>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSong(song: Song): Long

    @Delete
    fun deleteSong(song: Song): Int

}