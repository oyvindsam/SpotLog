package com.developments.samu.spotlog.service

import android.os.Build
import android.service.quicksettings.TileService
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.developments.samu.spotlog.SpotLogApplication
import com.developments.samu.spotlog.data.Song
import com.developments.samu.spotlog.data.SongRepository
import com.developments.samu.spotlog.utilities.Spotify
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.N)
class SpotLogTileService : TileService() {

    private val LOG_TAG: String = SpotLogTileService::class.java.simpleName

    @Inject
    lateinit var repository: SongRepository
    private val spotifyReceiver = Spotify.spotifyReceiver(::log)

    init {
        SpotLogApplication.getAppComponent().injectTileService(this)
    }

    override fun onClick() {
        this.registerReceiver(spotifyReceiver, Spotify.INTENT_FILTER)
        super.onClick()
    }

    override fun onStopListening() {
        try {
            this.unregisterReceiver(spotifyReceiver)
        } catch (e: Exception) {}
        super.onStopListening()
    }

    private fun log(song: Song) {
        repository.saveSong(song)
        Log.d(LOG_TAG, "Song: $song")
        Toast.makeText(this, "${song.track} by ${song.artist} logged", Toast.LENGTH_SHORT).show()
    }

}


