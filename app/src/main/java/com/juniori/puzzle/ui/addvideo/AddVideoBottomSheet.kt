package com.juniori.puzzle.ui.addvideo

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.juniori.puzzle.R
import com.juniori.puzzle.databinding.BottomsheetAddvideoBinding
import com.juniori.puzzle.ui.addvideo.camera.CameraActivity
import com.juniori.puzzle.util.VideoMetaDataUtil
import com.juniori.puzzle.util.readBytes
import com.juniori.puzzle.util.saveInFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddVideoBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomsheetAddvideoBinding? = null
    private val binding get() = _binding!!

    private val addVideoViewModel: AddVideoViewModel by activityViewModels()
    private var videoPickActivityLauncher: ActivityResultLauncher<Intent>? = null
    private var cameraActivityLauncher: ActivityResultLauncher<Intent>? = null

    private val videoMetaDataUtil get() = VideoMetaDataUtil

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
        cameraActivityLauncher?.launch(Intent(requireContext(), CameraActivity::class.java))
    }

    private fun initActivityLauncher() {
        videoPickActivityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val videoUri = result.data?.data ?: return@registerForActivityResult
                    val durationInSeconds: Long =
                        videoMetaDataUtil.getVideoDurationInSeconds(requireContext(), videoUri)
                            ?: return@registerForActivityResult
                    if (durationInSeconds > VIDEO_DURATION_LIMIT_SECONDS) {
                        showDurationLimitFeedback()
                        dismiss()
                        return@registerForActivityResult
                    }

                    // TODO: 실제 비디오 형식으로 이름 변경
                    lifecycleScope.launch {
                        val videoName = "temporary_video_name"
                        withContext(Dispatchers.IO) {
                            saveVideoInCacheDir(videoUri, videoName)
                        }
                        addVideoViewModel.setVideoName(videoName)
                        findNavController().navigate(R.id.fragment_upload_step1, arguments)
                    }
                }
            }

        cameraActivityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val videoNameInCacheDir = result.data?.getStringExtra(VIDEO_NAME_KEY)
                        ?: return@registerForActivityResult
                    addVideoViewModel.setVideoName(videoNameInCacheDir)
                    findNavController().navigate(R.id.fragment_upload_step1, arguments)
                }
            }
    }

    private fun showDurationLimitFeedback() {
        Toast.makeText(
            context,
            getString(R.string.addvideo_error_durationlimit),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun saveVideoInCacheDir(videoUri: Uri, videoName: String) {
        videoUri.readBytes(requireContext().contentResolver)?.let { videoBytes ->
            val videoCachePath = "${requireContext().cacheDir.path}/$videoName.mp4"
            videoBytes.saveInFile(videoCachePath)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val VIDEO_DURATION_LIMIT_SECONDS = 20
        const val VIDEO_NAME_KEY = "VIDEO_NAME_KEY"
    }
}
