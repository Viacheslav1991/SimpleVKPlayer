package com.android.simplevkplayer.util

object ProgressUtils {
    fun getTimeFromProgress(progress: Int, duration: Int): Int {
        return duration * progress / 1000
    }

    fun getSongProgress(totalDuration: Int, currentDuration: Int): Int {
        return currentDuration * 1000 / totalDuration
    }
}