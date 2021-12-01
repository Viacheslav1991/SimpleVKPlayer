package com.android.simplevkplayer.util

object TimeConverter {
    fun convertToTimerMode(songDuration: String): String {
        val duration = songDuration.toLong()
        val hour = duration / (1000 * 60 * 60)
        val minute = duration % (1000 * 60 * 60) / (1000 * 60)
        val seconds = duration % (1000 * 60 * 60) % (1000 * 60) / 1000
        var finalString = ""
        if (hour < 10) finalString += "0"
        finalString += "$hour:"
        if (minute < 10) finalString += "0"
        finalString += "$minute:"
        if (seconds < 10) finalString += "0"
        finalString += seconds
        return finalString
    }
}