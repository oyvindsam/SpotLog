package com.samudev.spotlog.log

import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
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
import com.samudev.spotlog.databinding.LogFragmentBinding
import com.samudev.spotlog.dependencyinjection.DaggerLogFragmentComponent
import com.samudev.spotlog.service.LoggerService
import com.samudev.spotlog.utilities.autoCleared
import kotlinx.android.synthetic.main.log_fragment.*
import javax.inject.Inject


class LogFragment : Fragment() {

    private val LOG_TAG: String = LogFragment::class.java.simpleName

    // starts same service, just different action
    private val loggerServiceIntentBackground by lazy { Intent(LoggerService.ACTION_START_BACKGROUND, Uri.EMPTY, context, LoggerService::class.java) }
    private val loggerServiceIntentForeground by lazy { Intent(LoggerService.ACTION_START_FOREGROUND, Uri.EMPTY, context, LoggerService::class.java) }


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var viewModel: SongLogViewModel

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    var binding by autoCleared<LogFragmentBinding>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val databinding = DataBindingUtil.inflate<LogFragmentBinding>(inflater, R.layout.log_fragment, container, false)
        binding = databinding

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)
        initDagger()
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SongLogViewModel::class.java)

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
                return true
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, position: Int) {
                viewModel.removeSong(viewHolder.itemView.tag as Song)
            }
        }).attachToRecyclerView(binding.songList)

        val adapter = LogAdapter() // Not injected by dagger yet since the adapter needs to be refactored
        binding.songList.adapter = adapter
        // Init list
        viewModel.songLog.observe(viewLifecycleOwner, Observer { songs ->
            if (songs != null) {
                adapter.submitList(songs)
                noHistoryTextView.visibility = if (songs.isEmpty()) View.VISIBLE else View.GONE // TODO: textview should observe a mutablelivedata in viewmodel
            }
        })

        if (sharedPreferences.getBoolean("FIRST_LAUNCH", true)) showEnableBroadcastDialog()
    }

    private fun showEnableBroadcastDialog() {
        if (isPackageInstalled(Spotify.PACKAGE_NAME, context?.packageManager)) {
            sharedPreferences.applyPref(Pair("FIRST_LAUNCH", false))
                    AlertDialog.Builder(context)
                            .setTitle(getString(R.string.dialog_broadcast_title))
                            .setMessage(getString(R.string.dialog_broadcast_message))
                            .setPositiveButton(getString(R.string.dialog_broadcast_positive)) { dialog, key ->
                                startActivity(context?.packageManager?.getLaunchIntentForPackage("com.spotify.music"))
                            }
                            .setNegativeButton(getString(R.string.dialog_broadcast_negative)) { dialog, key ->
                                dialog.cancel()
                            }
                            .show()
                }
        else {
            AlertDialog.Builder(context)
                    .setTitle(getString(R.string.dialog_package_title))
                    .setMessage(getString(R.string.dialog_package_message))
                    .setNegativeButton(getString(R.string.dialog_package_negative)) { dialog, key ->
                        dialog.cancel()
                        activity?.finish()
                    }
                    .show()
        }
    }

    private fun isPackageInstalled(packagename: String, packageManager: PackageManager?): Boolean {
        if (packageManager == null) return false
        try {
            packageManager.getPackageInfo(packagename, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            return false
        }
        return true
    }

    fun SharedPreferences.applyPref(pref: Pair<String, Any>) {
        val editor = this.edit()
        when(pref.second) {
            is Boolean -> editor.putBoolean(pref.first, pref.second as Boolean)
        }
        editor.apply()
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
        context?.startService(loggerServiceIntentBackground)
        viewModel.onResume()
    }

    override fun onPause() {
        super.onPause()
        // Stop backgroundservice in fragment
        context?.stopService(loggerServiceIntentBackground)
        // if foregroundservice is turned ON.. turn it on
        if (sharedPreferences.getBoolean(getString(R.string.pref_foreground_key), false)) context?.startService(loggerServiceIntentForeground)
        else context?.stopService(loggerServiceIntentForeground)
    }

    private fun initDagger() {
        DaggerLogFragmentComponent.builder()
                .appComponent(SpotLogApplication.getAppComponent())
                .build()
                .injectLogFragment(this)
    }

}
