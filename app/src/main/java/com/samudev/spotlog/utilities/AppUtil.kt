package com.samudev.spotlog.utilities

import android.content.SharedPreferences
import com.samudev.spotlog.preference.PrefsFragment

fun SharedPreferences.getIntOrDefault(key: String): Int {
    return when (key) {
        PrefsFragment.PREF_TIMEOUT_KEY -> this.getInt(key, PrefsFragment.PREF_TIMEOUT_DEFAULT)
        PrefsFragment.PREF_LOG_SIZE_KEY -> this.getString(key, PrefsFragment.PREF_LOG_SIZE_DEFAULT)?.toInt() ?: PrefsFragment.PREF_LOG_SIZE_DEFAULT.toInt()
        else -> throw IllegalArgumentException("Unknown key, $key")
    }
}

fun Int.minutesToMillis() = this * 1000 * 60  // convert minutes to milliseconds
