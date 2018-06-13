package com.samudev.spotlog.history

import com.samudev.spotlog.data.Song
import com.samudev.spotlog.data.db.Database


class HistoryPresenter(val historyView: HistoryContract.View) : HistoryContract.Presenter {

    init {
        historyView.presenter = this
    }

    override fun start() {
        loadSongs()
    }

    fun loadSongs() {
        val songs = Database.getSongs(5)
        historyView.showSongs(songs)
    }

    override fun handleSongClicked(song: Song) {
        historyView.showToast("Song item clicked: ${song.trackId}")
    }
}