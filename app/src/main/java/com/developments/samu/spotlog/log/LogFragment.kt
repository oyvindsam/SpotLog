package com.developments.samu.spotlog.log

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.developments.samu.spotlog.R
import com.developments.samu.spotlog.SpotLogApplication
import com.developments.samu.spotlog.data.toPrettyString
import com.developments.samu.spotlog.databinding.LogFragmentBinding
import com.developments.samu.spotlog.dependencyinjection.DaggerLogFragmentComponent
import com.developments.samu.spotlog.preference.PrefsFragment.Companion.PREF_FIRST_LAUNCH
import com.developments.samu.spotlog.service.LoggerService
import com.developments.samu.spotlog.utilities.Spotify
import com.developments.samu.spotlog.utilities.applyPref
import com.developments.samu.spotlog.utilities.isPackageInstalled
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import org.threeten.bp.LocalDate
import javax.inject.Inject


private val LOG_TAG: String = LogFragment::class.java.simpleName

class LogFragment : Fragment() {

    // starts same service, just different action
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
        viewModel = ViewModelProvider(this, viewModelFactory).get(SongLogViewModel::class.java)

        val adapter = LogAdapter(swipeCallback = {
            song -> viewModel.removeSong(song)
            Snackbar.make(binding.root, getString(R.string.action_song_deleted), Snackbar.LENGTH_SHORT).apply {
                setAction(R.string.action_undo) { _ -> viewModel.insertSong(song) }
            }.show()
        })

        binding.songList.adapter = adapter

        viewModel.run {
            songLog.observe(viewLifecycleOwner, Observer { songs ->
                if (songs != null) {
                    adapter.submitSongs(songs)
                }
            })
            isEmptyLog.observe(viewLifecycleOwner, Observer { isEmpty ->
                if (isEmpty != null) {
                    binding.noHistoryTextView.visibility = if (isEmpty) View.VISIBLE else View.GONE
                    binding.songList.visibility = if (isEmpty) View.GONE else View.VISIBLE
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
                    .setPositiveButton(getString(R.string.dialog_broadcast_positive)) { _, _ ->
                        // Intent.ACTION_APPLICATION_PREFERENCES added in api 24. On API < 24 it will just open Spotify.
                        val intent = Intent("android.intent.action.APPLICATION_PREFERENCES")
                        intent.`package` = Spotify.PACKAGE_NAME
                        startActivity(intent) }
                    .setNegativeButton(getString(R.string.dialog_broadcast_negative)) { dialog, _ ->
                        dialog.dismiss() }
                    .show()
        }
        else showSpotifyNotInstalledDialog()
    }

    private fun showSpotifyNotInstalledDialog() {
        AlertDialog.Builder(context)
                .setTitle(getString(R.string.dialog_package_title))
                .setMessage(getString(R.string.dialog_package_message))
                .setNegativeButton(getString(R.string.dialog_package_negative)) { dialog, _ ->
                    dialog.dismiss()
                    activity?.finish()
                }
                .show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.log_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_clear -> clearLog()
            R.id.menu_settings -> findNavController().navigate(R.id.action_to_settings)
            R.id.menu_filter -> showFilteringPopUpMenu()
            R.id.menu_export -> exportData()
        }
        return true
    }

    private fun clearLog() {
        viewModel.showLog(false)  // log is deleted if snackbar is dismissed
        Snackbar.make(binding.root, getString(R.string.action_log_cleared), Snackbar.LENGTH_LONG).apply {
            Log.d(LOG_TAG, "view: ${binding.root}")
            setAction(getString(R.string.action_undo)) { viewModel.showLog(true) }
            addCallback(object: Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    when (event) {
                        BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_TIMEOUT -> viewModel.clearSongs()
                        else ->  viewModel.showLog(true)
                    }
                    super.onDismissed(transientBottomBar, event)
                }
            })
        }.show()

    }

    private fun exportData() {
        val log = viewModel.songLog.value ?: return
        val title = getString(R.string.share_title) + " " + LocalDate.now()
        val body = title + "\n\n" + log.toPrettyString()
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, body)
            putExtra(Intent.EXTRA_TITLE, title)
        }
        startActivity(Intent.createChooser(intent, getString(R.string.share_title_full)))
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
        context?.startService(loggerServiceIntentForeground)
        viewModel.onResume()
    }

    override fun onPause() {
        super.onPause()
        if (!prefs.getBoolean(getString(R.string.pref_foreground_key), false)) {
            context?.stopService(loggerServiceIntentForeground)
        }
    }

    private fun initDagger() {
        DaggerLogFragmentComponent.builder()
                .appComponent(SpotLogApplication.getAppComponent())
                .build()
                .injectLogFragment(this)
    }

}
