package com.samudev.spotlog.log

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.samudev.spotlog.data.Song


class Spotify {
    companion object {
        const val SPOTIFY_PLAYBACK_STATE_CHANGED = "com.spotify.music.playbackstatechanged"
        val SPOTIFY_INTENT_FILTER = IntentFilter(SPOTIFY_PLAYBACK_STATE_CHANGED)

        // returns a broadcastreceiver with implemented callback
        fun spotifyReceiver(callback:((Song) -> Unit)): BroadcastReceiver {
            return object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (intent?.action == SPOTIFY_PLAYBACK_STATE_CHANGED) {
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
