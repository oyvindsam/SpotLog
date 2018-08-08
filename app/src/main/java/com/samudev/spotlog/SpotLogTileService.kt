package com.samudev.spotlog

import android.service.quicksettings.TileService
import com.samudev.spotlog.data.Song
import com.samudev.spotlog.data.SongRepository
import com.samudev.spotlog.history.Spotify
import com.samudev.spotlog.utilities.InjectorUtils

class SpotLogTileService : TileService() {

    private val LOG_TAG: String = SpotLogTileService::class.java.simpleName

    // TODO: Android kills service in ~1 min, make i foreground and optional in settings
    // private val loggerServiceIntent by lazy { Intent(applicationContext, LoggerService::class.java) }

    private var repository: SongRepository? = InjectorUtils.provideSongRepository(this)
    private val spotifyReceiver = Spotify.spotifyReceiver(::log)

    override fun onClick() {
        this.registerReceiver(spotifyReceiver, Spotify.SPOTIFY_INTENT_FILTER)
        super.onClick()
    }

    override fun onStopListening() {
        try {
            this.unregisterReceiver(spotifyReceiver)
        } catch (e: Exception) {}

        super.onStopListening()
    }

    private fun log(song: Song) {
        repository?.saveSong(song)
    }

}


