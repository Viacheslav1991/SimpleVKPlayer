package com.android.simplevkplayer.ui.list

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.simplevkplayer.util.TimeConverter
import com.android.simplevkplayer.domain.SongsRepository
import com.android.simplevkplayer.domain.model.player.RequestCall
import com.android.simplevkplayer.domain.model.player.Song
import com.android.simplevkplayer.ui.MainTimer
import com.android.simplevkplayer.util.ProgressUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongsListViewModel @Inject constructor(
    private val repository: SongsRepository
) : ViewModel() {

    val songsList: LiveData<RequestCall> get() = repository.fetchAllSongs()
    val currentSong: LiveData<RequestCall> get() = repository.getCurrentSong()
    @Inject
    lateinit var timer: MainTimer


    fun updateSongs() {
        viewModelScope.launch {
            repository.fetchAllSongs()
        }
    }

    fun moveSeekBar(progress: Int) {

    }

    fun play(mediaPlayer: MediaPlayer, song: Song): LiveData<RequestCall> {
        return repository.play(mediaPlayer, song)
    }

    fun pause(mediaPlayer: MediaPlayer, song: Song): LiveData<RequestCall> {
        return repository.pause(mediaPlayer, song)
    }


    fun getTimeFromProgress(progress: Int, duration: Int): Int {
        return ProgressUtils.getTimeFromProgress(progress, duration)
    }

    fun getSongProgress(totalDuration: Int, currentDuration: Int): Int {
        return ProgressUtils.getSongProgress(totalDuration, currentDuration)
    }

    fun convertToTimerMode(songDuration: String?): String? {
        return TimeConverter.convertToTimerMode(songDuration!!)
    }
}