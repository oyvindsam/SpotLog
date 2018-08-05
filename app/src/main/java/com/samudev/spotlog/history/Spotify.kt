package com.samudev.spotlog.history

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.samudev.spotlog.data.Song


class Spotify {
    companion object {
        const val SPOTIFY_PLAYBACK_STATE_CHANGED = "com.spotify.music.playbackstatechanged"
        val SPOTIFY_INTENT_FILTER = IntentFilter(SPOTIFY_PLAYBACK_STATE_CHANGED)

        // returns a broadcastreceiver with implemented callback
        fun spotifyReceiver(callback:((Song) -> Unit)): BroadcastReceiver {
            return object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (intent == null) return  // TODO: add check if this is the same context as then one who made the rec
                    if (intent.action == SPOTIFY_PLAYBACK_STATE_CHANGED) {
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
