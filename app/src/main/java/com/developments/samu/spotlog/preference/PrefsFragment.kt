package com.developments.samu.spotlog.preference

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.developments.samu.spotlog.R
import com.developments.samu.spotlog.SpotLogApplication
import com.developments.samu.spotlog.service.LoggerService
import javax.inject.Inject

private val LOG_TAG: String = PrefsFragment::class.java.simpleName

class PrefsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    lateinit var prefs: SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    private val loggerServiceIntentForeground by lazy { Intent(LoggerService.ACTION_START_FOREGROUND, Uri.EMPTY, context, LoggerService::class.java) }


    // TODO: register global-app broadcastreveivers in activity and skip this
    private fun toggleLoggerService(on: Boolean) {
        if (on) context?.startService(loggerServiceIntentForeground)
        else context?.stopService(loggerServiceIntentForeground)
    }

    override fun onDisplayPreferenceDialog(preference: Preference) {
        when (preference) {
            is TimeoutPreference -> {
                TimeoutPickerDialogFrag.newInstance(preference).also {
                    it.setTargetFragment(this, 0)
                    it.show(parentFragmentManager, "android.support.v7.preference.PreferenceFragment.DIALOG")
                }
            }
            else -> super.onDisplayPreferenceDialog(preference)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SpotLogApplication.getAppComponent().injectPrefsFragment(this)
    }

    // cannot be anon inner class: https://developer.android.com/guide/topics/ui/settings#Listening
    override fun onSharedPreferenceChanged(sharedPrefs: SharedPreferences?, key: String?) {
        if (sharedPrefs == null) return
        when (key) {
            getString(R.string.pref_foreground_key) -> toggleLoggerService(sharedPrefs.getBoolean(key, false))
        }
    }

    override fun onResume() {
        super.onResume()
        prefs.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        prefs.unregisterOnSharedPreferenceChangeListener(this)
    }

    companion object {
        const val PREF_LOG_SIZE_KEY = "log_size_key"
        const val PREF_LOG_SIZE_DEFAULT = "-1" // values are saved as a string-array of ints. must be converted later
        const val PREF_TIMEOUT_KEY = "timeout_key"
        const val PREF_TIMEOUT_DEFAULT = 10
        const val PREF_FIRST_LAUNCH = "first_launch_key"
    }
}