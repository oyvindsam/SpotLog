package com.samudev.spotlog.history

import com.samudev.spotlog.BasePresenter
import com.samudev.spotlog.BaseView
import com.samudev.spotlog.data.Song


interface HistoryContract {

    interface View : BaseView<Presenter> {
        fun showToast(message: String)
        fun showSongs(songs: List<Song>)
        fun showFilteringPopUpMenu()
    }

    interface Presenter : BasePresenter {
        fun handleSongClicked(song: Song)
        fun handleSongLongClicked(song: Song)
        fun handleSongBroadcastEvent(song: Song)
        fun handleSongSwiped(position: Int)
        fun loadSongs()
        fun saveSongs()
        fun clearHistory()
        var currentFiltering: Long
    }
}
