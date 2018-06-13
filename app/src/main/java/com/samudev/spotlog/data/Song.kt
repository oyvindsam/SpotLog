package com.samudev.spotlog.data


data class Song(
        var trackId: String = "",
        var artist: String = "",
        var album: String = "",
        var track: String = "",
        var trackLengthInSec: Int = 0)