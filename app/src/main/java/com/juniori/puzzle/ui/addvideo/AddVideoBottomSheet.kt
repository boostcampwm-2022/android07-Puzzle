package com.juniori.puzzle.ui.addvideo

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.juniori.puzzle.R
import com.juniori.puzzle.databinding.BottomsheetAddvideoBinding
import com.juniori.puzzle.ui.addvideo.camera.CameraActivity
import com.juniori.puzzle.ui.addvideo.upload.UploadStep1Fragment.Companion.THUMBNAIL_BYTE_ARRAY
import com.juniori.puzzle.ui.addvideo.upload.UploadStep1Fragment.Companion.VIDEO_FILE_PATH_KEY
import com.juniori.puzzle.util.readBytes
import com.juniori.puzzle.util.saveInFile
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddVideoBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomsheetAddvideoBinding? = null
    private val binding get() = _binding!!

    private val addVideoViewModel: AddVideoViewModel by viewModels()
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
                            val arguments = Bundle().apply {
                                putString(VIDEO_FILE_PATH_KEY, addVideoViewModel.videoFilePath)
                                putByteArray(THUMBNAIL_BYTE_ARRAY, addVideoViewModel.thumbnailBytes)
                            }
                            findNavController().navigate(R.id.fragment_upload_step1, arguments)
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
        }.run {
            videoPickActivityLauncher?.launch(this)
        }
    }

    private fun startCameraActivity() {
        cameraActivityLauncher?.launch(Intent(requireContext(), CameraActivity::class.java))
    }

    private fun initActivityLauncher() {
        videoPickActivityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val videoContentUri = result.data?.data ?: return@registerForActivityResult
                    val videoFilePath = "${requireContext().cacheDir}/${System.currentTimeMillis()}.mp4"
                    videoContentUri.readBytes(requireContext().contentResolver)
                        ?.saveInFile(videoFilePath) ?: return@registerForActivityResult
                    addVideoViewModel.notifyVideoPicked(videoFilePath)
                }
            }

        cameraActivityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val videoFilePath = result.data?.getStringExtra(VIDEO_NAME_KEY)
                        ?: return@registerForActivityResult
                    addVideoViewModel.notifyTakingVideoFinished(videoFilePath)
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
