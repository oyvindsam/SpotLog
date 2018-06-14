package com.samudev.spotlog.history

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import com.samudev.spotlog.R
import com.samudev.spotlog.data.Song
import com.samudev.spotlog.history.HistoryAdapter.HistoryItemListener

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [HistoryAdapter.HistoryItemListener] interface.
 */
class HistoryFragment : Fragment(), HistoryContract.View {

    private val LOG_TAG: String = HistoryFragment::class.java.simpleName

    override lateinit var presenter: HistoryContract.Presenter
    private lateinit var spotifyReceiver: SpotifyReceiver

    private lateinit var noHistoryTextView: TextView

    private var itemListener: HistoryItemListener = object : HistoryItemListener {
        override fun onSongClick(song: Song?) {
            if (song != null) presenter.handleSongClicked(song)
        }

        override fun onSongLongClick(song: Song?) {
            if (song != null) presenter.handleSongLongClicked(song)
        }
    }

    private var listAdapter = HistoryAdapter(ArrayList(0), itemListener)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.history_frag, container, false)

        with(rootView) {
            // Set the adapter
            val recyclerView = findViewById<RecyclerView>(R.id.list)
            with(recyclerView) {
                layoutManager = LinearLayoutManager(context)
                adapter = listAdapter
            }

            noHistoryTextView = findViewById(R.id.noHistoryTextView)
        }

        setHasOptionsMenu(true)

        spotifyReceiver = SpotifyReceiver()

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
                    R.id.one_hour -> presenter.currentFiltering = HistoryTimeFilter.ONE_HOUR
                    R.id.twelve_hours -> presenter.currentFiltering = HistoryTimeFilter.TWELVE_HOURS
                    else -> presenter.currentFiltering = HistoryTimeFilter.ALL
                }
                presenter.loadSongs()
                true
            }
            show()
        }
    }

    override fun onPause() {
        super.onPause()
        context?.unregisterReceiver(spotifyReceiver)
    }

    override fun onResume() {
        super.onResume()
        val spotifyIntent = IntentFilter("com.spotify.music.playbackstatechanged")
        context?.registerReceiver(spotifyReceiver, spotifyIntent)

        presenter.start()
    }

    override fun showSongs(songs: List<Song>) {
        listAdapter.songs = songs
        noHistoryTextView.visibility = if (songs.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun logSong(song: Song) {
        presenter.handleSongBroadcastEvent(song)
    }

    companion object {
        fun newInstance() = HistoryFragment()
    }

    inner class SpotifyReceiver : BroadcastReceiver() {
        val PLAYBACKSTATE_CHANGED = "com.spotify.music.playbackstatechanged"

        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null || context !is HistoryActivity) return
            if (intent.action.equals(PLAYBACKSTATE_CHANGED)) {
                val song = Song(
                        intent.getStringExtra("id"),
                        intent.getStringExtra("artist"),
                        intent.getStringExtra("album"),
                        intent.getStringExtra("track"),
                        intent.getIntExtra("length", 0),
                        System.currentTimeMillis()
                )
                logSong(song)
            }
        }
    }
}
