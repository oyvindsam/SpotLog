package com.samudev.spotlog.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(primaryKeys = ["track_id", "registered_time"])
data class Song(
        @ColumnInfo(name = "track_id") val trackId: String,
        val artist: String,
        val album: String,
        val track: String,
        @ColumnInfo(name = "track_length") val trackLengthInSec: Int,
        @ColumnInfo(name = "registered_time") val registeredTime: Long)