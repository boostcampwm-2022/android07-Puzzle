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
    private lateinit var exoPlayer: ExoPlayer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUploadStep1Binding.inflate(inflater, container, false)

        addVideoViewModel.videoName.observe(viewLifecycleOwner) { videoName ->
            val videoUriPath = "${requireContext().cacheDir.path}/$videoName.mp4"
            initVideoPlayer(videoUriPath)
        }
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

    private fun initVideoPlayer(uri: String) {
        exoPlayer = ExoPlayer.Builder(requireContext()).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
        }
        binding.videoplayer.player = exoPlayer
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        exoPlayer.release()
    }
}
