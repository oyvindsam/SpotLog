package com.samudev.spotlog.history


import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.samudev.spotlog.R
import com.samudev.spotlog.data.Song
import com.samudev.spotlog.history.HistoryAdapter.HistoryItemListener
import kotlinx.android.synthetic.main.song_log_item.view.*

/**
 * [ListAdapter] that can display a [Song] and makes a call to the
 * specified [HistoryItemListener].
 */
class HistoryAdapter(private val itemListener: HistoryItemListener?)
    : ListAdapter<Song, HistoryAdapter.ViewHolder>(SongDiffCallback()) {

    // listeners for the callback interface
    private val onItemClickListener: View.OnClickListener
    private val onItemLongClickListener: View.OnLongClickListener

    init {
        onItemClickListener = View.OnClickListener { v ->
            val item = v.tag as Song
            itemListener?.onSongClick(item)
        }

        onItemLongClickListener = View.OnLongClickListener { v ->
            val item = v.tag as Song
            // skip this for now.. itemListener?.onSongLongClick(item)
            true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.song_log_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }



    // Provides a reference to the views for each data item
    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(song: Song) {
            view.song.text = song.track
            view.artist.text = song.artist
            view.album.text = song.album
            view.tag = song
            view.setOnClickListener(onItemClickListener)
            view.setOnLongClickListener(onItemLongClickListener)
        }
    }

    interface HistoryItemListener {
        fun onSongClick(song: Song?)
        fun onSongLongClick(song: Song?)
    }
}
