package com.juniori.puzzle.ui.addvideo.upload

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

    private val cancelDialog: AlertDialog by lazy {
        createCancelDialog()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUploadStep1Binding.inflate(inflater, container, false).apply {
            vm = addVideoViewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonNext.setOnClickListener {
            addVideoViewModel.saveComments(binding.comments.text.toString())
            findNavController().navigate(R.id.fragment_upload_step2)
        }
        binding.buttonCancel.setOnClickListener {
            cancelDialog.show()
        }
    }

    override fun onStart() {
        super.onStart()
        initVideoPlayer()
    }

    override fun onStop() {
        super.onStop()
        releaseVideoPlayer()
        addVideoViewModel.saveComments(binding.comments.text.toString())
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
            addVideoViewModel.saveVideoPlayState(player.currentPosition, player.playWhenReady)
            player.release()
            exoPlayer = null
        }
    }

    private fun createCancelDialog(): AlertDialog {
        return MaterialAlertDialogBuilder(requireContext(), R.style.Theme_Puzzle_Dialog)
            .setTitle(R.string.upload_canceldialog_title)
            .setMessage(R.string.upload_canceldialog_supporting_text)
            .setPositiveButton(R.string.all_yes) { _, _ ->
                findNavController().navigateUp()
            }
            .setNegativeButton(R.string.all_no) { _, _ ->
                cancelDialog.dismiss()
            }
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
