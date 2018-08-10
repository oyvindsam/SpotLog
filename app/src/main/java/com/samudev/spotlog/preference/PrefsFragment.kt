package com.samudev.spotlog.preference

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import android.util.Log
import com.samudev.spotlog.R
import com.samudev.spotlog.SpotLogApplication
import javax.inject.Inject


class PrefsFragment : PreferenceFragmentCompat() {

    private val LOG_TAG: String = PrefsFragment::class.java.simpleName

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SpotLogApplication.getAppComponent().injectPrefsFragment(this)

        Log.d(LOG_TAG, "Default value: ${sharedPreferences.getBoolean("preference_foreground", true)}")
    }

    companion object {
        fun newInstance() = PrefsFragment()
    }
}