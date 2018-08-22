package com.samudev.spotlog.log

import android.arch.lifecycle.*
import android.content.SharedPreferences
import com.samudev.spotlog.data.Song
import com.samudev.spotlog.data.SongRepository
import com.samudev.spotlog.preference.PrefsFragment
import com.samudev.spotlog.utilities.getIntOrDefault
import javax.inject.Inject

private val LOG_TAG: String = SongLogViewModel::class.java.simpleName

class SongLogViewModel @Inject constructor(private val songRepository: SongRepository, private val prefs: SharedPreferences) : ViewModel() {

    private val logFilter = MutableLiveData<Long>()
    private val _songLog = MediatorLiveData<List<Song>>()

    private val _isEmptyLog = MediatorLiveData<Boolean>()

    init {
        logFilter.value = LogTimeFilter.ALL

        // Kinda obsolete, as the datasource never changes, only new values are added.
        // A more sane way to do this is to always call getSongLatest and implicitly filter based on logFilter value..
        val filteredSongLog = Transformations.switchMap(logFilter) {
            if (it == LogTimeFilter.ALL) songRepository.getSongsAll()
            else songRepository.getSongsLatest(it)
        }
        _songLog.addSource(filteredSongLog, _songLog::setValue)

        // Observer if filtered log is empty
        val noData: LiveData<Boolean> = Transformations.map(filteredSongLog) { it.isEmpty() }
        _isEmptyLog.addSource(noData, _isEmptyLog::setValue)
    }

    // only expose get() method
    val songLog: LiveData<List<Song>>
        get() = _songLog

    val isEmptyLog: LiveData<Boolean>
        get() = _isEmptyLog

    fun removeSong(song: Song) = songRepository.removeSong(song)

    fun clearSongs() = songRepository.clearSongs()

    fun setLogFilter(value: Long) {
        logFilter.value = when (value) {
            LogTimeFilter.ONE_MINUTE -> LogTimeFilter.ONE_MINUTE
            LogTimeFilter.ONE_HOUR -> LogTimeFilter.ONE_HOUR
            LogTimeFilter.TWELVE_HOURS -> LogTimeFilter.TWELVE_HOURS
            else -> LogTimeFilter.ALL
        }
    }

    fun onResume() {
        songRepository.removeOldSongs(prefs.getIntOrDefault(PrefsFragment.PREF_LOG_SIZE_KEY))
    }
}