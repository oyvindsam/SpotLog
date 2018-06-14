package com.samudev.spotlog.history


/**
 * Filter songs based on last logged time
 */
class HistoryTimeFilter {
    companion object {
        const val FIFTEEN_MINUTES = 900000
        const val ONE_HOUR = 3600000L
        const val TWELVE_HOURS = 12 * ONE_HOUR
        const val ALL = 9999999999

        fun getTimeAgo(time: Long): Long {
            return System.currentTimeMillis() - time
        }
    }
}