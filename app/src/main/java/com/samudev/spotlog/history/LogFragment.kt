package com.samudev.spotlog.history

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import com.samudev.spotlog.R
import com.samudev.spotlog.data.Song
import com.samudev.spotlog.history.LogAdapter.HistoryItemListener

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [LogAdapter.HistoryItemListener] interface.
 */
class LogFragment : Fragment(), LogContract.View {

    private val LOG_TAG: String = LogFragment::class.java.simpleName

    override lateinit var presenter: LogContract.Presenter
    private val spotifyReceiver = Spotify.spotifyReceiver(::logSong)

    private lateinit var noHistoryTextView: TextView

    private var itemListener: HistoryItemListener = object : HistoryItemListener {
        override fun onSongClick(song: Song?) {
            if (song != null) presenter.handleSongClicked(song)
        }

        override fun onSongLongClick(song: Song?) {
            if (song != null) presenter.handleSongLongClicked(song)
        }
    }

    private var listAdapter = LogAdapter(itemListener)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.history_frag, container, false)


        with(rootView) {
            // Set the adapter
            val recyclerView = findViewById<RecyclerView>(R.id.list)
            with(recyclerView) {
                layoutManager = LinearLayoutManager(context)
                adapter = listAdapter

                val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                    override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
                        return false
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
                        presenter.handleSongSwiped(viewHolder?.adapterPosition ?: 0)
                    }
                }
                val itemTouchHelper = ItemTouchHelper(swipeHandler)
                itemTouchHelper.attachToRecyclerView(this)
            }

            noHistoryTextView = findViewById(R.id.noHistoryTextView)
        }

        setHasOptionsMenu(true)

        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.history_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_clear -> presenter.clearHistory()
            R.id.menu_filter -> showFilteringPopUpMenu()
        }
        return true
    }

    override fun showFilteringPopUpMenu() {
        PopupMenu(context, activity?.findViewById(R.id.menu_filter)).apply {
            menuInflater.inflate(R.menu.filter_songs, menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.one_hour -> presenter.currentFiltering = LogTimeFilter.ONE_HOUR
                    R.id.twelve_hours -> presenter.currentFiltering = LogTimeFilter.TWELVE_HOURS
                    else -> presenter.currentFiltering = LogTimeFilter.ALL
                }
                presenter.loadSongs()
                true
            }
            show()
        }
    }

    override fun onPause() {
        super.onPause()
        // presenter.saveSongs() we do not do this just yet
        context?.unregisterReceiver(spotifyReceiver)
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
        context?.registerReceiver(spotifyReceiver, Spotify.SPOTIFY_INTENT_FILTER)

        presenter.start()
    }

    override fun showSongs(songs: List<Song>) {
        listAdapter.submitList(songs)
        noHistoryTextView.visibility = if (songs.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun logSong(song: Song) {
        Log.d(LOG_TAG, "Logged song inside app")
        presenter.handleSongBroadcastEvent(song)
    }

    companion object {
        fun newInstance() = LogFragment()
    }

}
