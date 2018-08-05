package com.samudev.spotlog.history

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.util.Log
import com.samudev.spotlog.data.Song
import com.samudev.spotlog.data.db.AppDatabase
import com.samudev.spotlog.data.db.SongDao
import com.samudev.spotlog.data.db.insertConditionally


class LogPresenter(db: AppDatabase, val logView: LogContract.View) : LogContract.Presenter {

    private val LOG_TAG: String = LogPresenter::class.java.simpleName
    private var songs = listOf<Song>()
    private var songDao: SongDao

    override var currentFiltering = LogTimeFilter.ALL

    init {
        logView.presenter = this
        songDao = db.songDao()

        // FIXME: dette virker som en real Hakk.
        if (logView is LifecycleOwner) {
            songDao.getAll().observe(logView, Observer<List<Song>> { songs ->
                Log.d(LOG_TAG, "Songs changed! $songs")
                loadSongs(songs)
            })
        }
    }

    override fun start() {
        loadSongs()
    }

    override fun loadSongs(newSongs: List<Song>?) {
        songs = newSongs ?: songDao.getLatest(System.currentTimeMillis() - currentFiltering)
        logView.showSongs(songs)
    }

    override fun clearHistory() {
        songDao.clearTable()
    }

    override fun handleSongClicked(song: Song) {
        logView.showToast("Song: ${song.track}")
    }

    override fun handleSongLongClicked(song: Song) {
        songDao.deleteSong(song)
    }

    override fun handleSongSwiped(position: Int) {
        songDao.deleteSong(songs.get(position))
    }

    override fun handleSongBroadcastEvent(song: Song) {
        songDao.insertConditionally(song, songs, LogTimeFilter.FIFTEEN_MINUTES)
    }
}