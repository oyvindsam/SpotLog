package com.samudev.spotlog

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.service.quicksettings.TileService
import android.util.Log
import android.widget.Toast
import com.samudev.spotlog.data.Song
import com.samudev.spotlog.history.HistoryFragment
import com.samudev.spotlog.history.Spotify

class SpotLogTileService : TileService() {

    private val LOG_TAG: String = SpotLogTileService::class.java.simpleName

    private val spotifyReceiver = Spotify.spotifyReceiver(::log)

    override fun onStopListening() {
        super.onStopListening()
        try {
            this.unregisterReceiver(spotifyReceiver)
        } catch (e: IllegalArgumentException) {}
    }

    override fun onClick() {
        super.onClick()
        this.registerReceiver(spotifyReceiver, Spotify.SPOTIFY_INTENT_FILTER)
    }

    fun log(song: Song) {
        Log.d(LOG_TAG, "Logged ${song.track} in tileService")
        Toast.makeText(this, "Soon logging this song ${song.track}", Toast.LENGTH_SHORT).show()
    }


}


