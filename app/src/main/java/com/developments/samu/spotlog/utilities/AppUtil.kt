package com.developments.samu.spotlog.utilities

import android.content.SharedPreferences
import android.content.pm.PackageManager
import com.developments.samu.spotlog.preference.PrefsFragment
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/*
  Welcome to the class with the weird stuff.
 */

fun SharedPreferences.getIntOrDefault(key: String): Int =
        when (key) {
            PrefsFragment.PREF_TIMEOUT_KEY -> this.getInt(key, PrefsFragment.PREF_TIMEOUT_DEFAULT)
            PrefsFragment.PREF_LOG_SIZE_KEY -> this.getString(key, PrefsFragment.PREF_LOG_SIZE_DEFAULT)?.toInt() ?: PrefsFragment.PREF_LOG_SIZE_DEFAULT.toInt()
            else -> throw IllegalArgumentException("Unknown key, $key")
        }

fun SharedPreferences.applyPref(pref: Pair<String, Any>) {
    val editor = this.edit()
    when (pref.second) {
        is Boolean -> editor.putBoolean(pref.first, pref.second as Boolean)
    }
    editor.apply()
}

fun Int.minutesToMillis() = this * 1000 * 60  // convert minutes to milliseconds

fun Long.toLocalDateTime(): LocalDateTime =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())

fun LocalDateTime.toReadableString(): String = this.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))

fun isPackageInstalled(packageName: String, packageManager: PackageManager?): Boolean =
        packageManager?.getPackageInfo(packageName, 0) != null ?: false
