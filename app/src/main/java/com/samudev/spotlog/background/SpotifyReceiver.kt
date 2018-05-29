package com.samudev.spotlog.background

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast


class SpotifyReceiver : BroadcastReceiver() {
    companion object {
        val METADATA_CHANGED = "com.spotify.music.metadatachanged"
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) return
        if (intent.action.equals(METADATA_CHANGED)) {
            Toast.makeText(context, "${intent.action} recevied", Toast.LENGTH_SHORT).show()

            val trackId = intent.getStringExtra("id")
            val artistName = intent.getStringExtra("artist")
            val albumName = intent.getStringExtra("album")
            val trackName = intent.getStringExtra("track")
            val trackLengthInSec = intent.getIntExtra("length", 0)
        }
    }
}