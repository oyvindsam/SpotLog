package com.developments.samu.spotlog.data

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["track_id", "registered_time"])
data class Song(
        @ColumnInfo(name = "track_id") val trackId: String,
        @ColumnInfo(name = "artist") val artist: String,
        @ColumnInfo(name = "album") val album: String,
        @ColumnInfo(name = "track") val track: String,
        @ColumnInfo(name = "track_length") val trackLengthInSec: Int,
        @ColumnInfo(name = "registered_time") val registeredTime: Long,
        @ColumnInfo(name = "playback_position") val playbackPosition: String)

fun Song.toPrettyString() =
        with (this) { "$track - $album - $artist"}

fun List<Song>.toPrettyString() =
        this.joinToString(separator = "\n") { song -> song.toPrettyString() }
