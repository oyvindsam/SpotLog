package com.samudev.spotlog

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import com.samudev.spotlog.data.Song
import com.samudev.spotlog.data.db.AppDatabase
import com.samudev.spotlog.history.Spotify

class LoggerService : Service() {

    private val spotifyReceiver = Spotify.spotifyReceiver(::log)

    private fun log(song: Song) {
        val id = AppDatabase.getAppDatabase(applicationContext).songDao().insertSong(song)
        if (id != 0L) Toast.makeText(this, "${song.track} successfully logged with id $id", Toast.LENGTH_SHORT).show()
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
