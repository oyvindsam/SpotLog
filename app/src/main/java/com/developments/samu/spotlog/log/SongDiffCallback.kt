package com.developments.samu.spotlog.log

import androidx.recyclerview.widget.DiffUtil
import com.developments.samu.spotlog.log.LogAdapter.*


class SongDiffCallback : DiffUtil.ItemCallback<ListItem>() {
    override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean =
            when {
                oldItem is SongItem && newItem is SongItem -> {
                    Pair(oldItem.song.trackId, oldItem.song.timeSent) == Pair(newItem.song.trackId, newItem.song.timeSent)
                }
                oldItem is HeaderItem && newItem is HeaderItem -> oldItem.date == newItem.date
                else -> false
            }

    // called when areItemsTheSame return true. Used to check if UI is the same. Can not compare
    // base objects since new ViewHolders are created on each update.
    override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean =
            when {
                oldItem is SongItem && newItem is SongItem -> oldItem.song == newItem.song
                oldItem is HeaderItem && newItem is HeaderItem -> oldItem.date == newItem.date
                else -> throw IllegalArgumentException("Should not have been possible to call this with two different objects!")
            }
}