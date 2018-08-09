package com.samudev.spotlog.viewmodels

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.samudev.spotlog.SpotLogApplication
import com.samudev.spotlog.data.Song
import com.samudev.spotlog.data.SongRepository
import com.samudev.spotlog.log.LogTimeFilter
import javax.inject.Inject


class SongLogViewModel : ViewModel() {

    @Inject
    lateinit var songRepository: SongRepository

    private val LOG_TAG: String = SongLogViewModel::class.java.simpleName

    private val logFilter = MutableLiveData<Long>()
    private val songLog = MediatorLiveData<List<Song>>()

    init {
        initDagger()
        logFilter.value = LogTimeFilter.ALL

        val filteredSongLog = Transformations.switchMap(logFilter) {
            if (it == LogTimeFilter.ALL) songRepository.getSongsAll()
            else songRepository.getSongsLatest(it)
        }
        songLog.addSource(filteredSongLog, songLog::setValue)
    }

    fun getSongs() = songLog

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


    // this is a 'hack' to avoid making our own factory method https://stackoverflow.com/questions/44270577/android-lifecycle-library-viewmodel-using-dagger-2
    private fun initDagger() = SpotLogApplication.getAppComponent().injectSongLogViewModel(this)
}