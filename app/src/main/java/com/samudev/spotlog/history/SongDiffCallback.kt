package com.samudev.spotlog.history

import android.support.v7.util.DiffUtil
import com.samudev.spotlog.data.Song


class SongDiffCallback : DiffUtil.ItemCallback<Song>() {
    override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem.trackId == newItem.trackId &&
                oldItem.registeredTime == newItem.registeredTime
    }

    override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem == newItem
    }
}