package com.samudev.spotlog.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(primaryKeys = ["track_id", "registered_time"])
data class Song(
        @ColumnInfo(name = "track_id") var trackId: String = "",
        var artist: String = "",
        var album: String = "",
        var track: String = "",
        @ColumnInfo(name = "track_length") var trackLengthInSec: Int = 0,
        @ColumnInfo(name = "registered_time") var registeredTime: Long = -1)