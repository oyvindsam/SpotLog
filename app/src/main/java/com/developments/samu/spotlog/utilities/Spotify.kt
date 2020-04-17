package com.developments.samu.spotlog.utilities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.util.Log
import com.developments.samu.spotlog.data.Song


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
                                with (intent) {
                                    val id = getStringExtra("id")
                                    val artist = getStringExtra("artist")
                                    val album = getStringExtra("album")
                                    val track = getStringExtra("track")
                                    val playbackPosition = getIntExtra("playbackPosition", 0)
                                    val length = getIntExtra("length", 0)
                                    val timeSent = getLongExtra("timeSent", -1L)
                                    if (id == null || artist == null || album == null || track == null) return
                                    val song = Song(
                                            id,
                                            artist,
                                            album,
                                            track,
                                            length,
                                            timeSent,
                                            playbackPosition
                                    )
                                    Log.d("Spotify", "song logged: $song")
                                    callback(song)
                                }
                            }
                        }
                    }
                }
    }

    fun Song.playIntent(): Intent =
            Intent(Intent.ACTION_VIEW).apply {
                `package` = Spotify.PACKAGE_NAME
                data = Uri.parse(Spotify.URI_WEB_TRACK + trackId.substring(14))
            }

