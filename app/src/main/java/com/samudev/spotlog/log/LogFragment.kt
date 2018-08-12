package com.samudev.spotlog.log

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.*
import android.widget.PopupMenu
import androidx.navigation.fragment.findNavController
import com.samudev.spotlog.R
import com.samudev.spotlog.SpotLogApplication
import com.samudev.spotlog.data.Song
import com.samudev.spotlog.dependencyinjection.DaggerLogFragmentComponent
import com.samudev.spotlog.service.LoggerService
import com.samudev.spotlog.viewmodels.SongLogViewModel
import kotlinx.android.synthetic.main.log_fragment.*
import javax.inject.Inject


class LogFragment : Fragment() {

    private val LOG_TAG: String = LogFragment::class.java.simpleName

    private val loggerServiceIntent by lazy { Intent(LoggerService.ACTION_START_BACKGROUND, Uri.EMPTY, context, LoggerService::class.java) }


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var viewModel: SongLogViewModel

    @Inject
    lateinit var listAdapter: LogAdapter


    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        initDagger()

        val rootView = inflater.inflate(R.layout.log_fragment, container, false)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SongLogViewModel::class.java)

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


        setHasOptionsMenu(true)
        return rootView
    }

    private fun subscribeUi(adapter: LogAdapter) {
        viewModel.getSongs().observe(viewLifecycleOwner, Observer { songs ->
            if (songs != null) {
                adapter.submitList(songs)
                noHistoryTextView.visibility = if (songs.isEmpty()) View.VISIBLE else View.GONE
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.log_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_clear -> viewModel.clearSongs()
            R.id.menu_settings -> findNavController().navigate(R.id.action_to_settings)
            R.id.menu_filter -> showFilteringPopUpMenu()
        }
        return true
    }

    private fun showFilteringPopUpMenu() {
        PopupMenu(context, activity?.findViewById(R.id.menu_filter)).apply {
            menuInflater.inflate(R.menu.filter_songs, menu)
            setOnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.one_minute -> viewModel.setLogFilter(LogTimeFilter.ONE_MINUTE)
                    R.id.one_hour -> viewModel.setLogFilter(LogTimeFilter.ONE_HOUR)
                    R.id.twelve_hours -> viewModel.setLogFilter(LogTimeFilter.TWELVE_HOURS)
                    else -> viewModel.setLogFilter(LogTimeFilter.ALL)
                }
                true
            }
            show()
        }
    }

    override fun onResume() {
        super.onResume()
        context?.startService(loggerServiceIntent)
    }

    override fun onPause() {
        super.onPause()
        context?.stopService(loggerServiceIntent)
        if (sharedPreferences.getBoolean("preference_foreground", false)) context?.startService(loggerServiceIntent.apply { this.action = LoggerService.ACTION_START_FOREGROUND })
    }

    private fun initDagger() {
        DaggerLogFragmentComponent.builder()
                .appComponent(SpotLogApplication.getAppComponent())
                .build()
                .injectLogFragment(this)
    }

    companion object {
        fun newInstance() = LogFragment()
    }


}
