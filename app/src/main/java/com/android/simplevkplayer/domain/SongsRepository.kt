package com.android.simplevkplayer.domain

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.simplevkplayer.domain.model.player.RequestCall
import com.android.simplevkplayer.domain.model.player.Song

interface SongsRepository {
    fun fetchAllSongs(): MutableLiveData<RequestCall>
    fun getCurrentSong(): LiveData<RequestCall>
    fun play(_mMediaPlayer: MediaPlayer?, song: Song): MutableLiveData<RequestCall>
    fun pause(mMediaPlayer: MediaPlayer?, song: Song): MutableLiveData<RequestCall>
    /*fun getTimeFromProgress(progress: Int, duration: Int): Int
    fun getSongProgress(totalDuration: Int, currentDuration: Int): Int
    fun convertToTimerMode(songDuration: String): String?*/
}