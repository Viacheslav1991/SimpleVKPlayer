package com.android.simplevkplayer.domain.model.player

import com.android.simplevkplayer.ui.Constants

 class RequestCall(){
     var status: Int = Constants.STOPPED
     var currentSong: Song = Song()
     var songs: ArrayList<Song> = ArrayList<Song>()
 }
