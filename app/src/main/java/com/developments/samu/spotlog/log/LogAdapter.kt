package com.developments.samu.spotlog.log


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.developments.samu.spotlog.data.Song
import com.developments.samu.spotlog.databinding.LogHeaderBinding
import com.developments.samu.spotlog.databinding.LogItemBinding
import com.developments.samu.spotlog.utilities.playIntent
import com.developments.samu.spotlog.utilities.toLocalDateTime
import com.developments.samu.spotlog.utilities.toReadableString
import org.threeten.bp.LocalDate
import java.text.DateFormat.getDateInstance
import java.util.*


class LogAdapter(private val swipeCallback: ((Song) -> Unit))
    : ListAdapter<LogAdapter.ListItem, RecyclerView.ViewHolder>(SongDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            when (viewType) {
                ListItem.TYPE_HEADER -> HeaderViewHolder(LogHeaderBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false))
                ListItem.TYPE_NORMAL -> SongViewHolder(LogItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false).apply {
                    titleText.isSingleLine = true  // Setting this in XML would cause a 'W/StaticLayout: maxLineHeight should not be -1' warning,
                    artistText.isSingleLine = true  // and make the app skip frames when scrolling
                    albumText.isSingleLine = true
                    timeText.isSingleLine = true

                })
                else -> throw IllegalArgumentException("unknown viewType: $viewType")
            }

    /**
     * @list songs sorted desc in time
     * Insert a header item for each new date
     * The generated list is then submitted to the adapter
     */
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
        this.submitList(songList)
    }

    // Setup swipe gestures
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder) = true
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, position: Int) {
                swipeCallback(viewHolder.itemView.tag as Song)  // headerItems can not be swiped
            }

            // remove swiping for header items
            override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                if (viewHolder is HeaderViewHolder) return ItemTouchHelper.ACTION_STATE_IDLE
                return super.getSwipeDirs(recyclerView, viewHolder)
            }
        }).attachToRecyclerView(recyclerView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (item) {
            is HeaderItem -> {
                (holder as HeaderViewHolder).apply {
                    bind(item.date)
                    itemView.tag = item.date
                }
            }
            is SongItem -> {
                val song = item.song
                (holder as SongViewHolder).apply {
                    bind(View.OnClickListener {
                        Log.d("log", "yep. logged1111111111111111111111111111111111111111")
                        Toast.makeText(it.context,
                                "${song.track} was logged ${song.registeredTime.toLocalDateTime().toReadableString()}",
                                Toast.LENGTH_SHORT).show() },
                            View.OnClickListener { it.context.startActivity(song.playIntent()) },
                            song)
                    itemView.tag = song
                }
            }
            else -> throw IllegalArgumentException("Unknown type; ${getItemViewType(position)}")
        }
    }

    override fun getItemViewType(position: Int): Int = getItem(position).type

    // Provides a reference to the views for each data item
    inner class SongViewHolder(private val binding: LogItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(listenerItem: View.OnClickListener, listenerPlay: View.OnClickListener, item: Song) {
            binding.apply {
                clickListenerItem = listenerItem
                clickListenerPlay = listenerPlay
                song = item
                executePendingBindings()
            }
        }
    }

    inner class HeaderViewHolder(private val binding: LogHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
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
