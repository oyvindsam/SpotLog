package com.samudev.spotlog.log


import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.samudev.spotlog.data.Song
import com.samudev.spotlog.databinding.LogHeaderBinding
import com.samudev.spotlog.databinding.LogItemBinding
import com.samudev.spotlog.utilities.toLocalDateTime
import com.samudev.spotlog.utilities.toReadableString
import java.text.DateFormat.getDateInstance
import java.time.LocalDate
import java.util.*

/**
 * [ListAdapter] that can display a [Song] and makes a call to the
 * specified [LogItemListener].
 */
class LogAdapter(private val swipeCallback: ((Song) -> Unit))
    : ListAdapter<LogAdapter.ListItem, RecyclerView.ViewHolder>(SongDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ListItem.TYPE_HEADER -> HeaderViewHolder(LogHeaderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false))
            ListItem.TYPE_NORMAL -> SongViewHolder(LogItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false))
            else -> throw IllegalArgumentException("unknown viewType: $viewType")
        }
    }

    fun submitSongs(list: List<Song>) {
        var lastDate: LocalDate? = null
        val songList = mutableListOf<ListItem>()
        list.forEach { song ->
            val date = song.registeredTime.toLocalDateTime().toLocalDate()  // group only on day-date
            if (date == lastDate) songList.add(SongItem(song))
            else {
                lastDate = date
                songList.addAll(listOf(HeaderItem(date), SongItem(song)))
            }
        }
        submitList(songList)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
                return true
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, position: Int) {
                swipeCallback(viewHolder.itemView.tag as Song)
            }

            // remove swiping for header items
            override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                if (viewHolder is HeaderViewHolder) return ItemTouchHelper.ACTION_STATE_IDLE
                return super.getSwipeDirs(recyclerView, viewHolder)
            }
        }).attachToRecyclerView(recyclerView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ListItem.TYPE_HEADER -> {
                val headerItem = getItem(position) as HeaderItem
                (holder as HeaderViewHolder).apply {
                    bind(headerItem.date)
                    itemView.tag = headerItem.date
                }
            }
            ListItem.TYPE_NORMAL -> {
                val songItem = getItem(position) as SongItem
                (holder as SongViewHolder).apply {
                    bind(View.OnClickListener { view ->
                        Toast.makeText(view.context,
                                "${songItem.song.track} was logged ${songItem.song.registeredTime.toLocalDateTime().toReadableString()}",
                                Toast.LENGTH_SHORT).show() },
                            songItem.song )
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

    class HeaderViewHolder(private val binding: LogHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LocalDate) {
            binding.apply {
                date = getDateInstance().format(Date(item.toEpochDay() * 60*60*24*1000))  // TODO: Fix this date mess
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

    class SongItem(var song: Song, override val type: Int = ListItem.TYPE_NORMAL) : ListItem

    class HeaderItem(var date: LocalDate, override val type: Int = ListItem.TYPE_HEADER) : ListItem

}
