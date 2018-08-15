package com.samudev.spotlog.preference

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import com.samudev.spotlog.R
import com.samudev.spotlog.SpotLogApplication
import com.samudev.spotlog.service.LoggerService
import javax.inject.Inject


class PrefsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val LOG_TAG: String = PrefsFragment::class.java.simpleName

    @Inject
    lateinit var sharedPreferences: SharedPreferences

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
            is NumberPickerPreference -> {
                NumberPickerDialogFrag.newInstance(preference).apply {
                    setTargetFragment(this, 0)
                    show(fragmentManager, "android.support.v7.preference.PreferenceFragment.DIALOG")
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
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}