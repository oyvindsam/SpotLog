package com.samudev.spotlog.history

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.*
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import com.samudev.spotlog.R
import com.samudev.spotlog.data.Song
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.log_fragment, container, false)

        // TODO: Dagger2
        val factory = InjectorUtils.provideSongLogViewModelFactory(requireContext())
        viewModel = ViewModelProviders.of(this, factory).get(SongLogViewModel::class.java)

        val listAdapter = LogAdapter()
        subscribeUi(listAdapter)

        val recyclerView = rootView.findViewById<RecyclerView>(R.id.song_list)
        recyclerView.adapter = listAdapter

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, position: Int) {
                viewModel.removeSong(viewHolder.itemView.tag as Song)
            }
        }).attachToRecyclerView(recyclerView)

        noHistoryTextView = rootView.findViewById(R.id.noHistoryTextView)

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

    private fun showFilteringPopUpMenu() {
        PopupMenu(context, activity?.findViewById(R.id.menu_filter)).apply {
            menuInflater.inflate(R.menu.filter_songs, menu)
            setOnMenuItemClickListener { item ->
                when(item.itemId) {
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
