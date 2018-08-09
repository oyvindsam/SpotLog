package com.samudev.spotlog

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.samudev.spotlog.data.Song
import com.samudev.spotlog.data.SongRepository
import com.samudev.spotlog.log.LogFragment
import com.samudev.spotlog.log.Spotify
import javax.inject.Inject

class LoggerService : Service() {

    private val LOG_TAG: String = LogFragment::class.java.simpleName

    private val spotifyReceiver = Spotify.spotifyReceiver(::log)

    @Inject
    lateinit var repository: SongRepository

    init {
        SpotLogApplication.getAppComponent().injectLoggerService(this)
    }
    private fun log(song: Song) {
        Log.d(LOG_TAG, "log called in LoggerService, context: $this")
        repository.logSong(song)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        registerReceiver(spotifyReceiver, Spotify.SPOTIFY_INTENT_FILTER)
        Toast.makeText(this, "LoggerService is on", Toast.LENGTH_SHORT).show()
        return START_STICKY
    }

    override fun onDestroy() {
        unregisterReceiver(spotifyReceiver)
        Toast.makeText(this, "LoggerService is off", Toast.LENGTH_SHORT).show()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }


}
