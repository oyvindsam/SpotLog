package com.developments.samu.spotlog.log

import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.SharedPreferences
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.PopupMenu
import androidx.navigation.fragment.findNavController
import com.developments.samu.spotlog.R
import com.developments.samu.spotlog.SpotLogApplication
import com.developments.samu.spotlog.databinding.LogFragmentBinding
import com.developments.samu.spotlog.dependencyinjection.DaggerLogFragmentComponent
import com.developments.samu.spotlog.preference.PrefsFragment.Companion.PREF_FIRST_LAUNCH
import com.developments.samu.spotlog.service.LoggerService
import com.developments.samu.spotlog.utilities.Spotify
import com.developments.samu.spotlog.utilities.applyPref
import com.developments.samu.spotlog.utilities.isPackageInstalled
import javax.inject.Inject


private val LOG_TAG: String = LogFragment::class.java.simpleName

class LogFragment : Fragment() {

    // starts same service, just different action
    private val loggerServiceIntentBackground by lazy { Intent(LoggerService.ACTION_START_BACKGROUND, Uri.EMPTY, context, LoggerService::class.java) }
    private val loggerServiceIntentForeground by lazy { Intent(LoggerService.ACTION_START_FOREGROUND, Uri.EMPTY, context, LoggerService::class.java) }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: SongLogViewModel

    @Inject
    lateinit var prefs: SharedPreferences

    private lateinit var binding: LogFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate<LogFragmentBinding>(inflater, R.layout.log_fragment, container, false)

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)
        initDagger()
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SongLogViewModel::class.java)

        val adapter = LogAdapter(swipeCallback = { song -> viewModel.removeSong(song) })
        binding.songList.adapter = adapter
        // Init list

        viewModel.run {
            songLog.observe(viewLifecycleOwner, Observer { songs ->
                if (songs != null) {
                    adapter.submitSongs(songs)
                }
            })
            isEmptyLog.observe(viewLifecycleOwner, Observer { isEmpty ->
                if (isEmpty != null) {
                    binding.noHistoryTextView.visibility = if (isEmpty) View.VISIBLE else View.GONE
                }
            })
        }

        if (prefs.getBoolean(PREF_FIRST_LAUNCH, true)) showEnableBroadcastDialog()
    }

    // If Spotify is installed; show a 'enable broadcast' dialog. If not
    private fun showEnableBroadcastDialog() {
        if (isPackageInstalled(Spotify.PACKAGE_NAME, context?.packageManager)) {
            prefs.applyPref(Pair(PREF_FIRST_LAUNCH, false))
            AlertDialog.Builder(context)
                    .setTitle(getString(R.string.dialog_broadcast_title))
                    .setMessage(getString(R.string.dialog_broadcast_message))
                    .setPositiveButton(getString(R.string.dialog_broadcast_positive)) { dialog, key ->
                        val intent = Intent(Intent.ACTION_APPLICATION_PREFERENCES)
                        intent.`package` = Spotify.PACKAGE_NAME
                        startActivity(intent) }
                    .setNegativeButton(getString(R.string.dialog_broadcast_negative)) { dialog, key ->
                        dialog.cancel() }
                    .show()
        }
        else showSpotifyNotInstalledDialog()
    }

    private fun showSpotifyNotInstalledDialog() {
        AlertDialog.Builder(context)
                .setTitle(getString(R.string.dialog_package_title))
                .setMessage(getString(R.string.dialog_package_message))
                .setNegativeButton(getString(R.string.dialog_package_negative)) { dialog, key ->
                    dialog.cancel()
                    activity?.finish()
                }
                .show()
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
        if (prefs.getBoolean(getString(R.string.pref_foreground_key), false)) context?.startService(loggerServiceIntentForeground)
        else context?.stopService(loggerServiceIntentForeground)
    }

    private fun initDagger() {
        DaggerLogFragmentComponent.builder()
                .appComponent(SpotLogApplication.getAppComponent())
                .build()
                .injectLogFragment(this)
    }

}
