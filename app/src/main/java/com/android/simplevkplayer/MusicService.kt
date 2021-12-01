package com.android.simplevkplayer

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.android.simplevkplayer.ui.list.SongsListViewModel

private const val TAG = "MusicService"

class MusicService : Service() {
    private var myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    lateinit var viewModel: SongsListViewModel

    override fun onBind(intent: Intent?): IBinder? {
        return myBinder
    }

    fun toDoSomething() {
        Log.i(TAG, ": toDoSomething");
        mediaPlayer?.stop()
    }

    fun bindViewModel(viewModel: SongsListViewModel) {
        this.viewModel = viewModel
    }

    inner class MyBinder : Binder() {
        fun getService(): MusicService {
            return this@MusicService
        }
    }


}