package com.samudev.spotlog.utilities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.util.Log
import com.samudev.spotlog.data.Song


class Spotify {


    companion object {
        const val PACKAGE_NAME = "com.spotify.music"
        const val PLAYBACK_STATE_CHANGED = "com.spotify.music.playbackstatechanged"
        const val URI_WEB = "https://open.spotify.com"
        const val URI_WEB_TRACK = "$URI_WEB/track/"
        val INTENT_FILTER = IntentFilter(PLAYBACK_STATE_CHANGED)

        // returns a broadcastreceiver with implemented callback
        fun spotifyReceiver(callback: ((Song) -> Unit)): BroadcastReceiver =
                object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        if (intent?.action == PLAYBACK_STATE_CHANGED) {
                            val song = Song(
                                    intent.getStringExtra("id"),
                                    intent.getStringExtra("artist"),
                                    intent.getStringExtra("album"),
                                    intent.getStringExtra("track"),
                                    intent.getIntExtra("length", 0),
                                    System.currentTimeMillis()
                            )
                            if (song.trackId.isEmpty()) return
                            callback(song)
                        }
                    }
                }
    }
}

fun Song.playIntent(): Intent =
        Intent(Intent.ACTION_VIEW).apply {
            Log.d("Song ", "id: ${trackId}")
            `package` = Spotify.PACKAGE_NAME
            data = Uri.parse(Spotify.URI_WEB_TRACK + trackId.substring(14))
            Log.d("Song ", "id: ${data}")

        }

