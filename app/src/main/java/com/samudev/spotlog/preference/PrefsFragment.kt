package com.samudev.spotlog.preference

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
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

    // cannot be anon inner class: https://developer.android.com/guide/topics/ui/settings#Listening
    override fun onSharedPreferenceChanged(sharedPrefs: SharedPreferences?, key: String?) {
        if (sharedPrefs == null) return
        when(key) {
            getString(R.string.pref_foreground_key) -> toggleLoggerService(sharedPrefs.getBoolean(key, false))
        }
    }

    private val loggerServiceIntentForeground by lazy { Intent(LoggerService.ACTION_START_FOREGROUND, Uri.EMPTY, context, LoggerService::class.java) }

    private fun toggleLoggerService(on: Boolean) {
        if (on) context?.startService(loggerServiceIntentForeground)
        else context?.stopService(loggerServiceIntentForeground)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // put this on onResume
        SpotLogApplication.getAppComponent().injectPrefsFragment(this)
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