package com.samudev.spotlog.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.samudev.spotlog.R
import com.samudev.spotlog.SpotLogApplication
import com.samudev.spotlog.data.Song
import com.samudev.spotlog.data.SongRepository
import com.samudev.spotlog.log.LogActivity
import com.samudev.spotlog.log.LogFragment
import com.samudev.spotlog.log.Spotify
import javax.inject.Inject


/**
 * Service for listening to intents sent by Spotify, and saving new songs to repository.
 * The service can either be foreground or background. When the user has the app open the background
 * service should run. If the user toggles 'Foreground Service' ON in Settings, the
 * app should start the foreground service when the user EXITS the app. A foreground service on API >= 26
 * needs to have an active notification to live. A background service will be killed in ~1 min if the user exits
 * the app.
 */
class LoggerService : Service() {

    private val LOG_TAG: String = LogFragment::class.java.simpleName

    private val spotifyReceiver = Spotify.spotifyReceiver(::log)

    // Foreground service stuff
    private val clickIntent by lazy { Intent(this, LogActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP} }

    private val pendingIntent by lazy { PendingIntent.getActivity(this, 0, clickIntent, 0) }
    private val notification by lazy {
        Notification.Builder(this, LoggerService.DEFAULT_CHANNEL)  // Default channel needs to be set up before adding notification
                .setSmallIcon(R.drawable.ic_tile_log_track)
                .setContentTitle("SpotLog")
                .setContentText("Logging Spotify songs...")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
    }

    @Inject
    lateinit var repository: SongRepository

    init {
        SpotLogApplication.getAppComponent().injectLoggerService(this)
    }

    private fun log(song: Song) {
        // TODO: show last logged song in notification
        Log.d(LOG_TAG, "log called in LoggerService, context: $this songRegistered: ${song.registeredTime}")
        repository.logSong(song)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("onStartCommand--------", "Intent action: ${intent?.action}, $flags, $startId")
        registerReceiver(spotifyReceiver, Spotify.SPOTIFY_INTENT_FILTER)

        if (intent?.action == LoggerService.ACTION_START_FOREGROUND) startForeground(1, notification)
        else stopForeground(true)

        return START_STICKY
    }

    override fun onDestroy() {
        try {
            // Throws if not started
            unregisterReceiver(spotifyReceiver)
        } catch (e: Exception) {}
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    companion object {
        const val ACTION_START_FOREGROUND = "START_FOREGROUND"
        const val ACTION_START_BACKGROUND = "START_BACKGROUND"
        const val DEFAULT_CHANNEL = "SPOTLOG_DEFAULT_CHANNEL"
        const val CHANNEL_NAME_LOGGER = "Foreground service logger"


        // Needs only to be called once
        fun createNotificationChannel(context: Context) {
            val channel = NotificationChannel(DEFAULT_CHANNEL, CHANNEL_NAME_LOGGER, NotificationManager.IMPORTANCE_LOW).apply {
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                description = "This is the visible notification when foreground-loggin is turned on."
            }
            val service = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(channel)
        }
    }


}
