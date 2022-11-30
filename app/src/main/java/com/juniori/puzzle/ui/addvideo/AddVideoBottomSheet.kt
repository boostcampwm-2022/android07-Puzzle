package com.juniori.puzzle.ui.addvideo

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.juniori.puzzle.R
import com.juniori.puzzle.databinding.BottomsheetAddvideoBinding
import com.juniori.puzzle.ui.addvideo.camera.CameraActivity
import com.juniori.puzzle.util.readBytes
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AddVideoBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomsheetAddvideoBinding? = null
    private val binding get() = _binding!!

    private val addVideoViewModel: AddVideoViewModel by activityViewModels()
    private var videoPickActivityLauncher: ActivityResultLauncher<Intent>? = null
    private var cameraActivityLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initActivityLauncher()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetAddvideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSearchGallery.setOnClickListener {
            startVideoPickActivity()
        }
        binding.buttonTakeVideo.setOnClickListener {
            startCameraActivity()
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                addVideoViewModel.uiState.collectLatest { uiState ->
                    if (uiState == null) return@collectLatest
                    when (uiState) {
                        AddVideoUiState.SHOW_DURATION_LIMIT_FEEDBACK -> {
                            showDurationLimitFeedback()
                        }
                        AddVideoUiState.GO_TO_UPLOAD -> {
                            findNavController().navigate(R.id.fragment_upload_step1)
                        }
                    }
                }
            }
        }
    }

    private fun startVideoPickActivity() {
        Intent().apply {
            type = "video/*"
            action = Intent.ACTION_PICK
            putExtra(MediaStore.EXTRA_DURATION_LIMIT, VIDEO_DURATION_LIMIT_SECONDS)
        }.run {
            videoPickActivityLauncher?.launch(this)
        }
    }

    private fun startCameraActivity() {
        cameraActivityLauncher?.launch(Intent(requireContext(), CameraActivity::class.java).apply {
            putExtra("uid", addVideoViewModel.getUid())
        })
    }

    private fun initActivityLauncher() {
        videoPickActivityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val videoUri = result.data?.data ?: return@registerForActivityResult
                    val videoBytes = videoUri.readBytes(requireContext().contentResolver)
                        ?: return@registerForActivityResult
                    addVideoViewModel.notifyAction(
                        AddVideoActionState.VideoPicked(videoUri, videoBytes)
                    )
                }
            }

        cameraActivityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val videoNameInCacheDir = result.data?.getStringExtra(VIDEO_NAME_KEY)
                        ?: return@registerForActivityResult
                    addVideoViewModel.notifyAction(
                        AddVideoActionState.TakingVideoCompleted(videoNameInCacheDir)
                    )
                }
            }
    }

    private fun showDurationLimitFeedback() {
        Snackbar.make(
            binding.root,
            getString(R.string.addvideo_error_durationlimit),
            Snackbar.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val VIDEO_DURATION_LIMIT_SECONDS = 20
        const val VIDEO_NAME_KEY = "VIDEO_NAME_KEY"
    }
}
