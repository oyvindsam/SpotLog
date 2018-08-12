package com.samudev.spotlog

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.samudev.spotlog.data.Song
import com.samudev.spotlog.data.SongRepository
import com.samudev.spotlog.log.LogActivity
import com.samudev.spotlog.log.LogFragment
import com.samudev.spotlog.log.Spotify
import javax.inject.Inject

class LoggerService : Service() {

    private val LOG_TAG: String = LogFragment::class.java.simpleName

    private val spotifyReceiver = Spotify.spotifyReceiver(::log)

    private val clickIntent by lazy { Intent(this, LogActivity::class.java) }

    private val pendingIntent by lazy { PendingIntent.getActivity(this, 0, clickIntent, 0) }
    private val notification by lazy {
        Notification.Builder(this, createNotificationChannel())
                .setSmallIcon(R.drawable.ic_tile_log_track)
                .setContentTitle("SpotLog")
                .setContentText("Logging Spotify songs...")
                .setContentIntent(pendingIntent)
                .build()
    }

    private fun createNotificationChannel(): String {
        val DEFAULT_CHANNEL = "SPOTLOG_DEFAULT_CHANNEL"
        val CHANNEL_NAME = "Spotlog"
        val channel = NotificationChannel(DEFAULT_CHANNEL,
                CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(channel)
        return DEFAULT_CHANNEL
    }

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
        startForeground(1, notification)
        return START_STICKY
    }

    override fun onDestroy() {
        //unregisterReceiver(spotifyReceiver)
        Log.d(LOG_TAG, "LoggerService destroyed, context: $this")
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }


}
