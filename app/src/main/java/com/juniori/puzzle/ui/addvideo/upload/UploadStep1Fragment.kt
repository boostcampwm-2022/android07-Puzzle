package com.juniori.puzzle.ui.addvideo.upload

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.juniori.puzzle.R
import com.juniori.puzzle.databinding.FragmentUploadStep1Binding
import com.juniori.puzzle.ui.addvideo.AddVideoViewModel

class UploadStep1Fragment : Fragment() {

    private var _binding: FragmentUploadStep1Binding? = null
    private val binding get() = _binding!!

    private val addVideoViewModel: AddVideoViewModel by activityViewModels()
    private var exoPlayer: ExoPlayer? = null
    private val mediaItem: MediaItem by lazy {
        MediaItem.fromUri("${requireContext().cacheDir.path}/${addVideoViewModel.videoName}.mp4")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUploadStep1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonNext.setOnClickListener {
            findNavController().navigate(R.id.action_uploadstep1_to_uploadstep2, arguments)
        }
        binding.buttonCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onStart() {
        super.onStart()
        initVideoPlayer()
    }

    override fun onStop() {
        super.onStop()
        releaseVideoPlayer()
    }

    private fun initVideoPlayer() {
        exoPlayer = ExoPlayer.Builder(requireContext()).build().also { player ->
            player.setMediaItem(mediaItem)
            player.seekTo(addVideoViewModel.playPosition)
            player.playWhenReady = addVideoViewModel.playWhenReady
            player.prepare()
            binding.videoplayer.player = player
        }
    }

    private fun releaseVideoPlayer() {
        exoPlayer?.let { player ->
            addVideoViewModel.saveVideoState(player.currentPosition, player.playWhenReady)
            player.release()
            exoPlayer = null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
