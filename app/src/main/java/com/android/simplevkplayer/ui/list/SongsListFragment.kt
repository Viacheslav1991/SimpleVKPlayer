package com.android.simplevkplayer.ui.list

import android.content.ComponentName
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.simplevkplayer.MusicService
import com.android.simplevkplayer.R
import com.android.simplevkplayer.domain.model.player.RequestCall
import com.android.simplevkplayer.domain.model.player.Song
import com.android.simplevkplayer.ui.Constants
import com.android.simplevkplayer.ui.adapters.SongsAdapter
import com.android.simplevkplayer.ui.player.PlayerFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SongsListFragment : Fragment(R.layout.main_fragment), SongsAdapter.SongClickListener,
    PlayerFragment.Callbacks, ServiceConnection {

    companion object {
        private const val POSITION = "position"
        private const val TAG = "SongsListFragment"

        fun newInstance(token: String): SongsListFragment {
            val args = Bundle()
            args.putSerializable(Constants.TOKEN, token)

            val fragment = SongsListFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val viewModel: SongsListViewModel by viewModels()
    private var currentSong: Song? = null
    private var currentSongPosition: Int = 0
    private var isPause = false
    private var musicService: MusicService? = null


    private var mediaPlayer: MediaPlayer? = null
        get() {
            if (field == null) {
                if (currentSong == null) {
                    return null
                }
                field = MediaPlayer.create(context, Uri.parse(currentSong?.data))
            }

            Log.i(TAG, "MediaPlayer: $field");
            musicService?.mediaPlayer = field
            return field
        }

    private lateinit var playerFragment: PlayerFragment
    private var recycler: RecyclerView? = null


    private val songsAdapter = SongsAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            currentSongPosition = savedInstanceState.getInt(POSITION)
        }
        //Service
        val intent = Intent(context, MusicService::class.java)
        context?.bindService(intent, this, BIND_AUTO_CREATE)
        context?.startService(intent)

        showPlayer()
        recycler = view.findViewById(R.id.rv_tracks)
        setUpSongsAdapter()

        viewModel.updateSongs()

        viewModel.songsList.observe(this.viewLifecycleOwner, this::updateAdapter)

        viewModel.timer.time.observe(this.viewLifecycleOwner, {
//            TODO()
            updateTimeInList(
                position = currentSongPosition!!,
                time = viewModel.convertToTimerMode(it.toString())
            )
        })


    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(POSITION, currentSongPosition)
    }

    private fun updateTimeInList(position: Int, time: String?) {
        songsAdapter.notifyItemChanged(position, time)
        Log.i(TAG, "Time in Fragment: $time");
        Log.i(TAG, "Position in Fragment: $position");
    }

    private fun updateAdapter(call: RequestCall) {
        songsAdapter.submitList(call.songs)
    }


    private fun setUpSongsAdapter() {
        recycler?.layoutManager = LinearLayoutManager(requireContext())
        recycler?.adapter = songsAdapter
    }

    private fun showPlayer() {
        playerFragment = PlayerFragment.newInstance()
        childFragmentManager.beginTransaction().replace(R.id.player_container, playerFragment)
            .commit()
    }

    override fun onNextClick() {
        Toast.makeText(context, "onNext clicked", Toast.LENGTH_SHORT).show()
        Log.i(TAG, ": $musicService");
        musicService?.toDoSomething()
    }

    override fun onPreviousClick() {
        Toast.makeText(context, "onPrev clicked", Toast.LENGTH_SHORT).show()
        TODO()
    }

    override fun onPlayPauseClick(_song: Song?) {
        var isContinue = true
        var song = _song
        if (song == null) {
            song = currentSong
            isContinue = false
        } else if (song != currentSong) {
            currentSong?.isPlaying = false
            mediaPlayer?.release()
            mediaPlayer = null
            isContinue = false
        }
        if (isPause) isContinue = true

        currentSong = song
        if (mediaPlayer?.isPlaying == true) {
            pauseSong()
        } else {
            playSong(isContinue)
        }

    }

    private fun playSong(isContinue: Boolean) {
        mediaPlayer?.let {
            currentSongPosition = songsAdapter.getPositionBaseOnItemID(currentSong?.id)
            isPause = false
            viewModel.timer.startMainTimer(!isContinue)
            val liveData = viewModel.play(it, currentSong!!)
            liveData.observe(this.viewLifecycleOwner, { requestCall ->
                if (requestCall.status == Constants.PLAYING) {
                    currentSong?.isPlaying = true
                    songsAdapter.notifyDataSetChanged()
                }
            })
        }
    }

    private fun pauseSong() {
        mediaPlayer?.let {
            viewModel.timer.pauseTimer()
            isPause = true
            val liveData = viewModel.pause(it, currentSong!!)
            liveData.observe(this.viewLifecycleOwner, { requestCall ->
                if (requestCall.status == Constants.STOPPED) {
                    currentSong?.isPlaying = false
                    songsAdapter.notifyDataSetChanged()
                }
            })
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.getService()
        Log.i(TAG, "MusicService: $musicService")
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
        Log.i(TAG, "MusicService: $musicService")
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}