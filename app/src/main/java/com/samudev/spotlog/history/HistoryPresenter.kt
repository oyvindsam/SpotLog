package com.samudev.spotlog.history

import com.samudev.spotlog.data.Song
import com.samudev.spotlog.data.db.AppDatabase
import com.samudev.spotlog.history.HistoryTimeFilter.Companion.FIFTEEN_MINUTES
import com.samudev.spotlog.history.HistoryTimeFilter.Companion.getTimeAgo
import kotlin.concurrent.thread


class HistoryPresenter(val db: AppDatabase, val historyView: HistoryContract.View) : HistoryContract.Presenter {

    private val LOG_TAG: String = HistoryPresenter::class.java.simpleName
    private var songs: MutableList<Song> = mutableListOf()  // TODO: statisk typing anyone
    private val songDao = db.songDao()

    override var currentFiltering = HistoryTimeFilter.ALL

    init {
        historyView.presenter = this
    }

    override fun start() {
        loadSongs()
    }

    override fun loadSongs() {
        // TODO: use thread?
        songs = songDao.getLatest(getTimeAgo(currentFiltering)).toMutableList()
        historyView.showSongs(songs)
    }

    override fun saveSongs() {
        // TODO: Figure out if it is feasible to only save songs in onPause/onStop..
        // now every song logged is instantly saved to disk
        // need two lists: one for recently added (not saved), and one for currently showing
        return
    }

    override fun clearHistory() {
        songDao.clearTable()
        loadSongs()
    }

    override fun handleSongClicked(song: Song) {
        historyView.showToast("Song: ${song.track}")
    }

    override fun handleSongLongClicked(song: Song) {
        songDao.deleteSong(song)
        loadSongs()
    }

    override fun handleSongSwiped(position: Int) {
        songDao.deleteSong(songs.get(position))
        loadSongs()
    }

    override fun handleSongBroadcastEvent(song: Song) {
        // basically check if song is in list and it's under 15 min since last added
        if (songs.stream().anyMatch { s -> s.trackId == song.trackId && song.registeredTime - s.registeredTime < FIFTEEN_MINUTES }) return
        songDao.insertSong(song)

        // livedata anyone?
        loadSongs()
    }
}