package com.samudev.spotlog.log


import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.samudev.spotlog.data.Song
import com.samudev.spotlog.databinding.LogHeaderBinding
import com.samudev.spotlog.databinding.LogItemBinding
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * [ListAdapter] that can display a [Song] and makes a call to the
 * specified [LogItemListener].
 */
class LogAdapter
    : ListAdapter<LogAdapter.ListItem, RecyclerView.ViewHolder>(SongDiffCallback()) {

    // listeners for the callback interface

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ListItem.TYPE_HEADER -> HeaderViewHolder(LogHeaderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false))
            ListItem.TYPE_NORMAL -> SongViewHolder(LogItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false))
            else -> throw IllegalArgumentException("unknown viewType: $viewType")
        }
    }

    /**
     * This method is literally shit. It is wrapped in a giant FIXME
     */
    fun submitSongs(list: List<Song>?) {
        if (list == null) return
        var lastDate: LocalDate? = null
        val songList = mutableListOf<ListItem>()
        list.forEach {
            val date = LocalDateTime.ofInstant(Instant.ofEpochMilli(it.registeredTime), ZoneId.systemDefault()).toLocalDate()
            if (lastDate == null) {
                lastDate = date
                songList.add(HeaderItem(date))
                songList.add(SongItem(it))
                Log.d("NEW DATE: ", ":: $date")
            }
            else if (date.month == lastDate?.month && date.dayOfMonth == lastDate?.dayOfMonth) songList.add(SongItem(it))
            else {
                songList.add(HeaderItem(date))
                songList.add(SongItem(it))
            }
        }
        submitList(songList)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ListItem.TYPE_HEADER -> {
                val headerItem = getItem(position) as HeaderItem
                (holder as HeaderViewHolder).apply {
                    bind(headerItem.dateTime)
                    itemView.tag = headerItem.dateTime
                }
            }
            ListItem.TYPE_NORMAL -> {
                val songItem = getItem(position) as SongItem
                (holder as SongViewHolder).apply {
                    bind(View.OnClickListener { _ ->
                        Log.d("Adapter", "${songItem.song.track} clicked!") },
                            songItem.song)
                    itemView.tag = songItem.song
                }
            }
            else -> throw IllegalArgumentException("Unknown type; ${getItemViewType(position)}")
        }

    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type
    }

    // Provides a reference to the views for each data item
    inner class SongViewHolder(private val binding: LogItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(listener: View.OnClickListener, item: Song) {
            binding.apply {
                clickListener = listener
                song = item
                executePendingBindings()
            }
        }
    }

    inner class HeaderViewHolder(private val binding: LogHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LocalDate) {
            binding.apply {
                date = item
                executePendingBindings()
            }
        }
    }

    // All listitems must implement this.
    interface ListItem {
        val type: Int

        companion object {
            const val TYPE_NORMAL = 0
            const val TYPE_HEADER = 1
        }
    }

    class SongItem(var song: Song) : ListItem {
        override val type = ListItem.TYPE_NORMAL
    }

    class HeaderItem(var dateTime: LocalDate) : ListItem {
            override val type = ListItem.TYPE_HEADER
    }




}
