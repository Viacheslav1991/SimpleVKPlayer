package com.android.simplevkplayer.data.player

import android.content.Context
import android.database.Cursor
import android.media.MediaPlayer
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.simplevkplayer.domain.SongsRepository
import com.android.simplevkplayer.domain.model.player.RequestCall
import com.android.simplevkplayer.domain.model.player.Song
import com.android.simplevkplayer.ui.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SongsRepositoryImpl @Inject constructor(
    @ApplicationContext private val appContext: Context
) : SongsRepository {
    private val currentSongLiveData = MutableLiveData<RequestCall>()

/*
    override suspend fun getSongs(): List<Song> {
        val songs = mutableListOf<Song>()
//        songs.add(Song("FirstSong", "some url", "1", false))
//        songs.add(Song("SecondSong", "some url", "2", isPlaying = false))
//        songs.add(Song("ThirdSong", "some url", "3", false))

        return songs
    }
*/

    private fun convertToSong(cursor: Cursor): Song {
        return Song(
            id = cursor.getString(0),
            artist = cursor.getString(1),
            title = cursor.getString(2),
            data = cursor.getString(3),
            displayName = cursor.getString(4),
            duration = cursor.getString(5),
            isPlaying = false
        )
    }

    override fun fetchAllSongs(): MutableLiveData<RequestCall> {
        val r = RequestCall()
        r.status = Constants.LOADING
        val mLiveData = MutableLiveData<RequestCall>()
        val songs = ArrayList<Song>()
        r.songs = songs
        mLiveData.value = r
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION
        )

        val cursor = appContext.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null
        )

        while (cursor?.moveToNext() == true) {
            songs.add(convertToSong(cursor))
        }
        r.status = Constants.STOPPED
        r.songs = songs
        mLiveData.postValue(r)
        return mLiveData
    }

    override fun getCurrentSong(): LiveData<RequestCall> {
        return currentSongLiveData
    }

    override fun play(
        _mMediaPlayer: MediaPlayer?,
        song: Song
    ): MutableLiveData<RequestCall> {
        var mMediaPlayer = _mMediaPlayer
        val requestCall = RequestCall()
        requestCall.status = Constants.STOPPED
        requestCall.songs = ArrayList<Song>()
        val mutableLiveData = MutableLiveData<RequestCall>()
        mutableLiveData.value = requestCall

        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(appContext, Uri.parse(song.data))
        }
        mMediaPlayer!!.start()
        requestCall.status = Constants.PLAYING
        requestCall.currentSong = song
        mutableLiveData.postValue(requestCall)

        currentSongLiveData.value = requestCall
        mutableLiveData.postValue(requestCall)

        return mutableLiveData
    }

    override fun pause(mMediaPlayer: MediaPlayer?, song: Song): MutableLiveData<RequestCall> {
        var mMediaPlayer = mMediaPlayer
        val requestCall = RequestCall()
        requestCall.songs = ArrayList<Song>()
        val mutableLiveData = MutableLiveData<RequestCall>()
        mutableLiveData.value = requestCall
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(appContext, Uri.parse(song.data))
        } else {
            mMediaPlayer.pause()
            requestCall.status = Constants.STOPPED
            mutableLiveData.postValue(requestCall)
        }

        requestCall.currentSong = song
        currentSongLiveData.value = requestCall
        mutableLiveData.postValue(requestCall)
        return mutableLiveData
    }

    /*override fun getTimeFromProgress(progress: Int, duration: Int): Int {
        return duration * progress / 1000
    }

    override fun getSongProgress(totalDuration: Int, currentDuration: Int): Int {
        return currentDuration * 1000 / totalDuration
    }

    override fun convertToTimerMode(songDuration: String): String? {
        Log.i(TAG, "String duration: $songDuration");
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
    }*/

    companion object {
        private const val TAG = "SongsRepositoryImpl"
    }
}