package com.android.simplevkplayer.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.simplevkplayer.R
import com.android.simplevkplayer.util.TimeConverter
import com.android.simplevkplayer.domain.model.player.Song
import javax.inject.Inject

class SongsAdapter @Inject constructor(private val listener: SongClickListener) :
    ListAdapter<Song, SongViewHolder>(SongDiffCallback()) {
    interface SongClickListener {
        fun onPlayPauseClick(_song: Song?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder =
        SongViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_rv_track, parent, false)
        )

    override fun onBindViewHolder(
        holder: SongViewHolder,
        position: Int,
        payload: MutableList<Any>
    ) {
        holder.bind(getItem(position), listener)
        if (payload.isNotEmpty()) {
            payload.forEach {
                if (it is String) {
                    holder.setTime(it)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    fun getPositionBaseOnItemID(theID: String?): Int {
        val length: Int = itemCount
        for (i in 0 until length) {
            if (getItem(i).id.equals(theID)) {
                return i
            }
        }
        return -1 //Item not found
    }

}

class SongViewHolder @Inject constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val nameSong: TextView = itemView.findViewById(R.id.tv_item_name)
    private val time: TextView = itemView.findViewById(R.id.tv_item_time)
    private val playButton: ImageButton = itemView.findViewById(R.id.btn_item_play)
    fun bind(song: Song, listener: SongsAdapter.SongClickListener) {
        nameSong.text = song.displayName
        playButton.setOnClickListener { song.let { listener.onPlayPauseClick(it) } }
        if (song.isPlaying) {
            playButton.setImageResource(android.R.drawable.ic_media_pause)
        } else {
            playButton.setImageResource(android.R.drawable.ic_media_play)
        }
        time.text = TimeConverter.convertToTimerMode(songDuration = song.duration!!)
    }

    fun setTime(_time: String) {
        time.text = _time
    }
}


private class SongDiffCallback : DiffUtil.ItemCallback<Song>() {

    private val payload = Any()

    override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean =
        (oldItem.id == newItem.id)

    override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean =
        (oldItem == newItem)

    override fun getChangePayload(oldItem: Song, newItem: Song): Any = payload
}