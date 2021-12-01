package com.android.simplevkplayer.ui.player

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.android.simplevkplayer.R
import com.android.simplevkplayer.databinding.PlayerFragmentBinding
import com.android.simplevkplayer.domain.model.player.RequestCall
import com.android.simplevkplayer.domain.model.player.Song
import com.android.simplevkplayer.ui.list.SongsListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerFragment : Fragment(R.layout.player_fragment) {

    interface Callbacks {
        fun onPlayPauseClick(_song: Song?)
        fun onNextClick()
        fun onPreviousClick()
    }

    private var _binding: PlayerFragmentBinding? = null

    private val viewModel: SongsListViewModel by viewModels()

    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    private lateinit var callbacks: Callbacks

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is Callbacks) {
            callbacks = parentFragment as Callbacks
        }
    }

    companion object {
        fun newInstance() = PlayerFragment()
        const val TAG = "PlayerFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PlayerFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnPlay.setOnClickListener { callbacks.onPlayPauseClick(null) }
        binding.btnNextSong.setOnClickListener { callbacks.onNextClick() }
        binding.btnPrevSong.setOnClickListener { callbacks.onPreviousClick() }

        //seekBar
        setUpSeekBar()

        //timer
        viewModel.timer.time.observe(this.viewLifecycleOwner, {
            binding.tvTime.text = viewModel.convertToTimerMode(it.toString())
        })

        viewModel.currentSong.observe(this.viewLifecycleOwner, this::updateView)

    }

    private fun setUpSeekBar() {
        binding.seekBar.apply {
            min = 0
            max = 1000
        }
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Ваш код

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        viewModel.timer.time.observe(this.viewLifecycleOwner, {
            val total = viewModel.currentSong.value?.currentSong?.duration?.toInt()
            binding.seekBar.progress = viewModel.getSongProgress(
                totalDuration = total!!,
                currentDuration = it?.toInt()!!
            )

        })
    }

    private fun updateView(requestCall: RequestCall) {
        binding.tvSongName.text = requestCall.currentSong.title
//        binding.tvTime.text = requestCall.currentSong.duration
        binding.btnPlay.setImageResource(
            when (requestCall.currentSong.isPlaying) {
                true -> android.R.drawable.ic_media_play
                false -> android.R.drawable.ic_media_pause
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}


