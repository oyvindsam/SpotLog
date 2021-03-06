package com.developments.samu.spotlog.utilities

import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.text.format.DateUtils.formatElapsedTime
import com.developments.samu.spotlog.preference.PrefsFragment
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

/*
  Welcome to the class with the weird stuff.
 */

fun SharedPreferences.getIntOrDefault(key: String): Int =
        when (key) {
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
        try {
            packageManager?.getPackageInfo(packageName, 0) != null ?: false
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }

fun convertSecondsToHMmSs(seconds: Long): String? {
    val s = seconds % 60
    val m = seconds / 60 % 60
    val h = seconds / (60 * 60) % 24
    return if (h > 0) String.format("%d:%02d:%02d", h, m, s)
    else String.format("%d:%02d", m, s)
}

fun toMinLeft(length: Int, playbackPosition: Int): String {
    val lengthSec = length / 1000L
    val positionSec = playbackPosition / 1000L
    return "${convertSecondsToHMmSs(positionSec)} - ${convertSecondsToHMmSs(lengthSec)}"
}