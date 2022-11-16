package com.juniori.puzzle.ui.addvideo

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.juniori.puzzle.R
import com.juniori.puzzle.databinding.BottomsheetAddvideoBinding

class AddVideoBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomsheetAddvideoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddVideoViewModel by viewModels()
    private var videoPickActivityLauncher: ActivityResultLauncher<Intent>? = null

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
    }

    private fun initActivityLauncher() {
        videoPickActivityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val videoUri = result.data?.data ?: return@registerForActivityResult
                    val durationInSeconds: Long =
                        getVideoDurationInSeconds(videoUri) ?: return@registerForActivityResult
                    if (durationInSeconds > VIDEO_DURATION_LIMIT_SECONDS) {
                        showDurationLimitFeedback()
                        dismiss()
                        return@registerForActivityResult
                    }

                    // TODO videoUri 촬영 후 화면으로 전달
                }
            }
    }

    private fun getVideoDurationInSeconds(videoUri: Uri): Long? {
        val metaDataRetriever = MediaMetadataRetriever()
        metaDataRetriever.setDataSource(context, videoUri)
        return metaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            ?.let { milliseconds: String ->
                milliseconds.toLong() / 1000
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

    private fun showDurationLimitFeedback() {
        Toast.makeText(
            context,
            getString(R.string.addvideo_error_durationlimit),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val VIDEO_DURATION_LIMIT_SECONDS = 20
    }
}
