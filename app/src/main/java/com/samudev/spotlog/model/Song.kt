package com.samudev.spotlog.model


data class Song(
        var trackId: String = "",
        var artistName: String = "",
        var albumName: String = "",
        var trackName: String = "",
        var trackLengthInSec: Int = 0)