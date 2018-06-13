package com.samudev.spotlog.history

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.samudev.spotlog.R


import com.samudev.spotlog.data.Song

import kotlinx.android.synthetic.main.song_log_item.view.*

/**
 * [RecyclerView.Adapter] that can display a [Song] and makes a call to the
 * specified [HistoryItemListener].
 */
class HistoryAdapter(songs: List<Song>,
                     private val itemListener: HistoryItemListener?)
    : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    var songs: List<Song> = songs
        set(songs) {
            field = songs
            notifyDataSetChanged()
        }

    private val onItemListener: View.OnClickListener

    init {
        onItemListener = View.OnClickListener { v ->
            val item = v.tag as Song
            // Notify the active callbacks interface that an item has been selected.
            itemListener?.onSongClick(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.song_log_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = songs[position]
        holder.songView.text = song.track
        holder.artistView.text = song.artist
        holder.albumView.text = song.album

        with(holder.view) {
            tag = song
            setOnClickListener(onItemListener)
        }
    }

    override fun getItemCount(): Int = songs.size

    // Provides a reference to the views for each data item
    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val songView: TextView = view.song
        val artistView: TextView = view.artist
        val albumView: TextView = view.album

        override fun toString(): String {
            return super.toString() + " '" + artistView.text + "'"
        }
    }

    interface HistoryItemListener {
        fun onSongClick(song: Song?)
    }
}
