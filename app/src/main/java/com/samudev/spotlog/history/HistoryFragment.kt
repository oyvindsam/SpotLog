package com.samudev.spotlog.history

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private lateinit var noHistoryTextView: TextView

    private var itemListener: HistoryItemListener = object : HistoryItemListener {
        override fun onSongClick(song: Song?) {
            if (song != null) presenter.handleSongClicked(song)
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

        return rootView
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun showSongs(songs: List<Song>) {
        listAdapter.songs = songs
        listAdapter.notifyDataSetChanged()
        noHistoryTextView.visibility = if (songs.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun logSong(song: Song) {
        showToast(song.track)
    }

    companion object {
        fun newInstance() = HistoryFragment()
    }
}
