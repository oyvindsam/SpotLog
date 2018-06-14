package com.samudev.spotlog.history

import com.samudev.spotlog.data.Song
import com.samudev.spotlog.data.db.AppDatabase
import com.samudev.spotlog.history.HistoryTimeFilter.Companion.FIFTEEN_MINUTES
import com.samudev.spotlog.history.HistoryTimeFilter.Companion.getTimeAgo


class HistoryPresenter(val db: AppDatabase, val historyView: HistoryContract.View) : HistoryContract.Presenter {

    private val LOG_TAG: String = HistoryPresenter::class.java.simpleName
    private var songs: MutableList<Song> = mutableListOf()
    private val songDao = db.songDao()

    override var currentFiltering = HistoryTimeFilter.ALL

    init {
        historyView.presenter = this
        songs = songDao.getAll().toMutableList()
    }

    override fun start() {
        loadSongs()
    }

    override fun loadSongs() {
        songs = songDao.getLatest(getTimeAgo(currentFiltering)).toMutableList()
        historyView.showSongs(songs)
    }

    override fun clearHistory() {
        db.clearAllTables()
        loadSongs()
    }

    override fun handleSongClicked(song: Song) {
        historyView.showToast("Song item clicked: ${song.track}")
    }

    override fun handleSongLongClicked(song: Song) {
        songDao.deleteSong(song)
        loadSongs()
    }

    override fun handleSongBroadcastEvent(song: Song) {
        // basically check if song is in list and it's under 15 min since last added
        if (songs.stream().anyMatch { s -> s.trackId == song.trackId && song.registeredTime - s.registeredTime < FIFTEEN_MINUTES }) return

        // TODO: only save songs to db on onPause/onStop, get them on onResume
        songDao.insertSong(song)
        songs = songDao.getAll().toMutableList()
        historyView.showSongs(songs)
    }
}