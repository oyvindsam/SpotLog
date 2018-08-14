package com.samudev.spotlog.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
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
    private val serviceIntent by lazy { Intent(this, LoggerService::class.java).apply {
        action = LoggerService.ACTION_STOP
    } }

    private val pendingIntent by lazy { PendingIntent.getActivity(this, 0, clickIntent, 0) }
    private val pendingIntentStop by lazy { PendingIntent.getService(this, 0, serviceIntent, 0) }
    private val action by lazy {
        NotificationCompat.Action.Builder(
                R.drawable.ic_filter_list,
                "Stop",
                pendingIntentStop)
                .build()
    }

    private val notification by lazy {
        NotificationCompat.Builder(this, LoggerService.DEFAULT_CHANNEL)  // Default channel needs to be set up before adding notification
                .setSmallIcon(R.drawable.ic_tile_log_track)
                .setContentTitle(getString(R.string.notif_content_title))
                .setContentText(getString(R.string.notif_content_text))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(action)
    }

    @Inject
    lateinit var repository: SongRepository

    init {
        SpotLogApplication.getAppComponent().injectLoggerService(this)
    }

    private fun log(song: Song) {
        Log.d(LOG_TAG, "log called in LoggerService, context: $this songRegistered: ${song.registeredTime}")
        repository.logSong(song, ::notifySongLogged)
    }

    private fun notifySongLogged(song: Song) {
        notification.setContentText("${song.track} - ${song.artist}")
        NotificationManagerCompat.from(this).notify(LoggerService.NOTIFICATION_ID, notification.build())
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("onStartCommand--------", "Intent action: ${intent?.action}, $flags, $startId")
        when(intent?.action) {
            LoggerService.ACTION_STOP -> stopSelf()
            LoggerService.ACTION_START_FOREGROUND -> startService(true)
            LoggerService.ACTION_START_BACKGROUND -> startService(false)

        }
        return START_STICKY
    }

    private fun startService(foreground: Boolean) {
        registerReceiver(spotifyReceiver, Spotify.SPOTIFY_INTENT_FILTER)
        if (foreground) startForeground(LoggerService.NOTIFICATION_ID, notification.build())
        else stopForeground(true)

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
        const val ACTION_STOP = "STOP_SERVICE"
        const val DEFAULT_CHANNEL = "SPOTLOG_DEFAULT_CHANNEL"
        const val NOTIFICATION_ID = 3245


        // Needs only to be called once
        fun createNotificationChannel(context: Context) {
            val channel = NotificationChannel(
                    LoggerService.DEFAULT_CHANNEL,
                    context.getString(R.string.notif_channel_name),
                    NotificationManager.IMPORTANCE_LOW).apply {
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                description = context.getString(R.string.notif_channel_description)
            }
            val service = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(channel)
        }
    }


}
