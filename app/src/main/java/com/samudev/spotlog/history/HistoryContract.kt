package com.samudev.spotlog.history

import com.samudev.spotlog.BasePresenter
import com.samudev.spotlog.BaseView
import com.samudev.spotlog.data.Song


interface HistoryContract {

    interface View : BaseView<Presenter> {
        fun showToast(message: String)
        fun logSong(song: Song)
        fun showSongs(songs: List<Song>)

    }

    interface Presenter : BasePresenter {
        fun handleSongClicked(song: Song)
        fun handleSongBroadcastEvent(song: Song)
    }
}
