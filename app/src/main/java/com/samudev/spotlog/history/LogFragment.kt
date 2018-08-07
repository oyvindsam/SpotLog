package com.samudev.spotlog.history

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
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
import com.samudev.spotlog.history.LogAdapter.LogItemListener
import com.samudev.spotlog.utilities.InjectorUtils
import com.samudev.spotlog.viewmodels.SongLogViewModel

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [LogAdapter.LogItemListener] interface.
 */
class LogFragment : Fragment() {

    private val LOG_TAG: String = LogFragment::class.java.simpleName

    private val spotifyReceiver = Spotify.spotifyReceiver(::logSong)

    private lateinit var noHistoryTextView: TextView

    private lateinit var viewModel: SongLogViewModel

    private var itemListener: LogItemListener = object : LogItemListener {
        override fun onSongClick(song: Song?) {
            if (song != null) Log.d(LOG_TAG, "${song.track} Clicked")
        }

        override fun onSongLongClick(song: Song?) {
            if (song != null) Log.d(LOG_TAG, "${song.track} Lock clicked")
        }
    }

    private var listAdapter = LogAdapter(itemListener)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.history_frag, container, false)

        val factory = InjectorUtils.provideSongLogViewModelFactory(requireContext())
        viewModel = ViewModelProviders.of(this, factory).get(SongLogViewModel::class.java)

        subscribeUi(listAdapter)

        with(rootView) {
            // Set the adapter
            val recyclerView = findViewById<RecyclerView>(R.id.list)
            with(recyclerView) {
                layoutManager = LinearLayoutManager(context)
                adapter = listAdapter

                val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                    override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
                        return true
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, position: Int) {
                        viewModel.removeSong(viewHolder.itemView.tag as Song)
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

    private fun subscribeUi(adapter: LogAdapter) {
        viewModel.getSongs().observe(viewLifecycleOwner, Observer { songs ->
            if (songs != null) {
                adapter.submitList(songs)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.history_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_clear -> viewModel.clearSongs()
            R.id.menu_filter -> showFilteringPopUpMenu()
        }
        return true
    }

    fun showFilteringPopUpMenu() {
        PopupMenu(context, activity?.findViewById(R.id.menu_filter)).apply {
            menuInflater.inflate(R.menu.filter_songs, menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.one_hour -> viewModel.setLogFilter(LogTimeFilter.ONE_HOUR)
                    R.id.twelve_hours -> viewModel.setLogFilter(LogTimeFilter.TWELVE_HOURS)
                    else -> viewModel.setLogFilter(LogTimeFilter.ALL)
                }
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
        context?.registerReceiver(spotifyReceiver, Spotify.SPOTIFY_INTENT_FILTER)
    }

    fun logSong(song: Song) {
        Toast.makeText(context, "${song.track} clicked", Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newInstance() = LogFragment()
    }

}
