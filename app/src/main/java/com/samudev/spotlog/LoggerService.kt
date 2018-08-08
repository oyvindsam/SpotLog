package com.samudev.spotlog

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import com.samudev.spotlog.data.Song
import com.samudev.spotlog.data.SongRepository
import com.samudev.spotlog.history.Spotify
import com.samudev.spotlog.utilities.InjectorUtils

class LoggerService : Service() {

    private val spotifyReceiver = Spotify.spotifyReceiver(::log)
    private var repository: SongRepository? = InjectorUtils.provideSongRepository(this)

    private fun log(song: Song) {
        Toast.makeText(this, "${song.track} logged, id: ", Toast.LENGTH_SHORT).show()
        repository?.logSong(song)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        registerReceiver(spotifyReceiver, Spotify.SPOTIFY_INTENT_FILTER)
        Toast.makeText(this, "LoggerService is on", Toast.LENGTH_SHORT).show()
        return START_STICKY
    }

    override fun onDestroy() {
        unregisterReceiver(spotifyReceiver)
        repository = null
        Toast.makeText(this, "LoggerService is off", Toast.LENGTH_SHORT).show()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}
