package com.samudev.spotlog.log

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.samudev.spotlog.data.Song


class Spotify {
    companion object {
        const val PACKAGE_NAME = "com.spotify.music"
        const val PLAYBACK_STATE_CHANGED = "com.spotify.music.playbackstatechanged"
        val INTENT_FILTER = IntentFilter(PLAYBACK_STATE_CHANGED)

        // returns a broadcastreceiver with implemented callback
        fun spotifyReceiver(callback:((Song) -> Unit)): BroadcastReceiver {
            return object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (intent?.action == PLAYBACK_STATE_CHANGED) {
                        Log.d("Spotify_Receiver", " : " + intent.getLongExtra("timeSent", 0L))
                        val song = Song(
                                intent.getStringExtra("id"),
                                intent.getStringExtra("artist"),
                                intent.getStringExtra("album"),
                                intent.getStringExtra("track"),
                                intent.getIntExtra("length", 0),
                                System.currentTimeMillis()
                        )
                        callback(song)
                    }
                }
            }
        }
    }
}
