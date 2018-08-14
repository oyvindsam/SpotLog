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

    private val notifTapIntent by lazy { Intent(this, LogActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP} }
    private val notifStopIntent by lazy { Intent(this, LoggerService::class.java).apply {
        action = LoggerService.ACTION_STOP
    } }

    private val notifPendingIntent by lazy { PendingIntent.getActivity(this, 0, notifTapIntent, 0) }
    private val notifPendingStop by lazy { PendingIntent.getService(this, 0, notifStopIntent, 0) }
    private val notifActionStop by lazy {
        NotificationCompat.Action.Builder(
                R.drawable.ic_filter_list,
                getString(R.string.notif_action_title),
                notifPendingStop)
                .build()
    }

    private val notification by lazy {
        NotificationCompat.Builder(this, LoggerService.DEFAULT_CHANNEL)  // Default channel needs to be set up before adding notification
                .setSmallIcon(R.drawable.ic_tile_log_track)
                .setContentTitle(getString(R.string.notif_content_title))
                .setContentText(getString(R.string.notif_content_text))
                .setContentIntent(notifPendingIntent)
                .addAction(notifActionStop)
    }

    @Inject
    lateinit var repository: SongRepository

    private var notificationIsActive = false
    private var lastSong: Song? = null

    init {
        SpotLogApplication.getAppComponent().injectLoggerService(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            LoggerService.ACTION_STOP -> { stopSelf(); notificationIsActive = false; }
            LoggerService.ACTION_START_FOREGROUND -> startService(true)
            LoggerService.ACTION_START_BACKGROUND -> startService(false)
        }
        return START_STICKY
    }

    // start backgroundReceiver. Start foreground service if specified
    private fun startService(foreground: Boolean) {
        registerReceiver(spotifyReceiver, Spotify.INTENT_FILTER)  // start backgroundReceiver for picking up Spotify intents
        if (foreground) startForegroundNotif()
        else stopForeground(true)  // remove notification
        notificationIsActive = foreground
    }

    // start foreground service and check for latest logged song to put in notification content text.
    private fun startForegroundNotif() {
        notificationIsActive = true
        startForeground(LoggerService.NOTIFICATION_ID, notification.build())
        repository.getLastLoggedSong(::notifySongLogged)
    }

    private fun log(song: Song) {
        lastSong = song
        Log.d(LOG_TAG, "Log called, context: $this songRegistered: ${song.track} ${song.registeredTime}")
        repository.logSong(song, ::notifySongLogged)
        // TODO: show all songs picked up or only those (conditionally) saved to db?
        //if (lastSong != song) notifySongLogged(song)
    }

    private fun notifySongLogged(song: Song?) {
        if (!notificationIsActive || song == null) return  // chekc if notification is currently active
        notification.setContentText("${song.track} - ${song.artist}")
        NotificationManagerCompat.from(this).notify(LoggerService.NOTIFICATION_ID, notification.build())
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


        fun getNotification(context: Context) {

        }

        // Needs only to be called once (application startup)
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
