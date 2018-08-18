package com.samudev.spotlog.log

import android.support.v7.util.DiffUtil
import com.samudev.spotlog.log.LogAdapter.*


class SongDiffCallback : DiffUtil.ItemCallback<ListItem>() {
    override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
        return if (oldItem is SongItem && newItem is SongItem) {
            oldItem.song.trackId == newItem.song.trackId &&
                    oldItem.song.registeredTime == newItem.song.registeredTime
        } else if (oldItem is HeaderItem && newItem is HeaderItem) {
            oldItem.dateTime == newItem.dateTime
        }
        else false
    }

    // called when areItemsTheSame return true. Used to check if UI is the same. Can not compare
    // base objects since new ViewHolders are created on each update.
    override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
        return if (oldItem is SongItem && newItem is SongItem) oldItem.song == newItem.song
        else if (oldItem is HeaderItem && newItem is HeaderItem) oldItem.dateTime == newItem.dateTime
        else throw IllegalArgumentException("Should not have been possible to call this with two different objects!")
    }
}