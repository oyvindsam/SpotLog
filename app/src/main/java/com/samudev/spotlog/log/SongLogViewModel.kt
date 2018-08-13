package com.samudev.spotlog.log

import android.arch.lifecycle.*
import android.util.Log
import com.samudev.spotlog.data.Song
import com.samudev.spotlog.data.SongRepository
import javax.inject.Inject


class SongLogViewModel @Inject constructor(val songRepository: SongRepository) : ViewModel() {

    private val LOG_TAG: String = SongLogViewModel::class.java.simpleName

    private val logFilter = MutableLiveData<Long>()
    private val _songLog = MediatorLiveData<List<Song>>()

    init {
        Log.d(LOG_TAG, "CREATED")
        logFilter.value = LogTimeFilter.ALL

        // Kinda obsolete, as the datasource never changes, only new values are added.
        // A more sane way to do this is to always call getSongLatest and implicitly filter based on logFilter value..
        val filteredSongLog = Transformations.switchMap(logFilter) {
            if (it == LogTimeFilter.ALL) songRepository.getSongsAll()
            else songRepository.getSongsLatest(it)
        }
        _songLog.addSource(filteredSongLog, _songLog::setValue)
    }

    val songLog: LiveData<List<Song>>
        get() = _songLog

    fun removeSong(song: Song) = songRepository.removeSong(song)

    fun clearSongs() = songRepository.clearSongs()

    fun setLogFilter(value: Long) {
        logFilter.value = when(value) {
            LogTimeFilter.ONE_MINUTE -> LogTimeFilter.ONE_MINUTE
            LogTimeFilter.ONE_HOUR -> LogTimeFilter.ONE_HOUR
            LogTimeFilter.TWELVE_HOURS -> LogTimeFilter.TWELVE_HOURS
            else -> LogTimeFilter.ALL
        }
    }

    override fun onCleared() {
        Log.d(LOG_TAG, "OnCleared, Context: $this")

        super.onCleared()
    }
}