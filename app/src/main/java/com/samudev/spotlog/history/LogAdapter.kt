package com.samudev.spotlog.history


import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.samudev.spotlog.data.Song
import com.samudev.spotlog.databinding.LogItemBinding

/**
 * [ListAdapter] that can display a [Song] and makes a call to the
 * specified [LogItemListener].
 */
class LogAdapter
    : ListAdapter<Song, LogAdapter.ViewHolder>(SongDiffCallback()) {

    // listeners for the callback interface

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LogItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = getItem(position)
        holder.apply {
            bind(View.OnClickListener { v ->
                Log.d("Adapter", "${song.track} clicked!") },
                    song)
            itemView.tag = song
        }
    }

    // Provides a reference to the views for each data item
    inner class ViewHolder(private val binding: LogItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(listener: View.OnClickListener, item: Song) {
            binding.apply {
                clickListener = listener
                song = item
                executePendingBindings()
            }

        }
    }

}
