package com.developments.samu.spotlog.data

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface SongDao {

    @Query("SELECT * FROM song ORDER BY registered_time DESC")
    fun getAll(): LiveData<List<Song>>

    @Query("SELECT * FROM song WHERE registered_time > :fromTime ORDER BY registered_time DESC")
    fun getLatest(fromTime: Long): LiveData<List<Song>>

    @Query("SELECT * FROM song WHERE registered_time > :fromTime ORDER BY registered_time DESC")
    fun getLatestNoLiveData(fromTime: Long): List<Song>

    @Query("SELECT * FROM song ORDER BY registered_time DESC LIMIT 1")
    fun getLastLoggedSong(): Song?

    @Query("DELETE FROM song")
    fun clearTable()

    @Insert
    fun insertAll(songs: List<Song>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSong(song: Song): Long

    @Delete
    fun deleteSong(song: Song): Int

    // SqlLite does not support ORDER BY in DELETE FROM query.
    @Query("DELETE FROM song WHERE registered_time NOT IN (SELECT registered_time FROM song ORDER BY registered_time DESC LIMIT :logSize)")
    fun deleteOld(logSize: Int): Int
}