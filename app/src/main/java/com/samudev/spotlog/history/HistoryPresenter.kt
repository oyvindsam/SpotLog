package com.samudev.spotlog.history

import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import android.util.Log
import com.samudev.spotlog.data.Song
import com.samudev.spotlog.data.db.Database


class HistoryPresenter(val historyView: HistoryContract.View) : HistoryContract.Presenter {

    companion object {
        private val FIFTEEN_MINUTES = 900000
    }

    private val LOG_TAG: String = HistoryPresenter::class.java.simpleName

    private var songs: MutableList<Song> = mutableListOf<Song>()

    init {
        historyView.presenter = this
        songs = Database.getSongs(2)
    }

    override fun start() {
        loadSongs()
    }

    fun loadSongs() {
        historyView.showSongs(songs)
    }

    override fun handleSongClicked(song: Song) {
        historyView.showToast("Song item clicked: ${song.trackId}")
    }

    override fun handleSongBroadcastEvent(song: Song) {
        // basically check if song is in list and it's under 15 min since last added
        if (songs.stream().anyMatch { s -> s.trackId == song.trackId && song.registeredTime - s.registeredTime < FIFTEEN_MINUTES }) return
        songs.add(song)
        historyView.showSongs(songs)
    }
}