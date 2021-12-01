package com.android.simplevkplayer.domain.model.player

import java.text.FieldPosition

data class Song(
    val id: String? = null,
    val artist: String? = null,
    val title: String? = null,
    val data: String? = null,
    val displayName: String? = null,
    val duration: String? = null,
    var isPlaying: Boolean = false
)

