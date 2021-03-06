package com.developments.samu.spotlog.log

import androidx.lifecycle.*
import android.content.SharedPreferences
import com.developments.samu.spotlog.data.Song
import com.developments.samu.spotlog.data.SongRepository
import com.developments.samu.spotlog.preference.PrefsFragment
import com.developments.samu.spotlog.utilities.getIntOrDefault
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
            if (it == LogTimeFilter.NONE) MutableLiveData<List<Song>>()
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
    fun insertSong(song: Song) = songRepository.saveSong(song)

    fun clearSongs() = songRepository.clearSongs()
    fun showLog(show: Boolean) {
        if (show) setLogFilter(LogTimeFilter.ALL)
        else setLogFilter(LogTimeFilter.NONE)
    }

    fun setLogFilter(value: Long) {
        logFilter.value = when (value) {
            LogTimeFilter.NONE -> LogTimeFilter.NONE
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